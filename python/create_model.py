import pandas as pd
import xgboost as xgb
import sklearn
import argparse

from sklearn.multioutput import MultiOutputRegressor

parser = argparse.ArgumentParser(description='Train model for cryptocurrency.')
parser.add_argument("-s", help="symbol of currency", type=str)

args = parser.parse_args()
symbol = args.s.strip()

# against too big volatility
short_window_size = 1  # short-term view
long_window_size = 4  # longer term view


# build some features - sma is smooth average over short term and smal is smooth average over long term
def get_predictor(data):
    predictors = pd.DataFrame({"open_sma": data.open.rolling(window=short_window_size).mean(),
                               "open_smal": data.open.rolling(window=long_window_size).mean(),

                               "low_sma": data.low.rolling(window=short_window_size).mean(),
                               "low_smal": data.low.rolling(window=long_window_size).mean(),

                               "close_sma": data.close.rolling(window=short_window_size).mean(),
                               "close_smal": data.close.rolling(window=long_window_size).mean(),

                               "high_sma": data.high.rolling(window=short_window_size).mean(),
                               "high_smal": data.high.rolling(window=long_window_size).mean()}
                              )

    predictors["volume"] = data.volume + 0.001  # avoid null division
    # The rows with nulls generated by rolling values will be removed.
    predictors = predictors.dropna()

    return predictors


def get_x_and_y(p):
    # Equal weight of short window and long window
    target = pd.DataFrame({"low_value": ((p.low_sma.shift(-1) - p.low_sma) + (p.low_smal.shift(-1) - p.low_smal)) / 2,
                           "open_value": ((p.open_sma.shift(-1) - p.open_sma) + (
                                       p.open_smal.shift(-1) - p.open_smal)) / 2,
                           "close_value": ((p.close_sma.shift(-1) - p.close_sma) + (
                                       p.close_smal.shift(-1) - p.close_smal)) / 2,
                           "high_value": ((p.high_sma.shift(-1) - p.high_sma) + (
                                       p.high_smal.shift(-1) - p.high_smal)) / 2,
                           "volume_rel": (p.volume / p.volume.shift(-1))}).dropna()

    # split into input (X) and output (y)
    X = pd.merge(p, target, left_index=True, right_index=True)[p.columns]
    y = pd.merge(p, target, left_index=True, right_index=True)[target.columns]

    return X, y


# Load the CSV file
MAIN_DATA = pd.read_csv("data/data-" + symbol + ".csv", sep=",", header=0, decimal='.')
MAIN_DATA = MAIN_DATA.set_index("date")

# Add features
X, y = get_x_and_y(get_predictor(MAIN_DATA))

# Reshaping
no_of_entries = int(X.shape[0] * 1)
X_train = X.iloc[:no_of_entries]
y_train = y.iloc[:no_of_entries]

# Create a multi XGB regressor on all features
multioutputregressor = MultiOutputRegressor(xgb.XGBRegressor(objective='reg:linear')).fit(X_train, y_train)
print(y_train)

# the last set needs to include the last entry + the size of the long window for mean calculactions
p = -1 * (long_window_size + 1)
last_set = MAIN_DATA.iloc[p:]

# if there are any results
any_results_flag = False

# predict a time frame of 20 days in future
for y in range(20):
    # add features to this data set
    X_test, y_test = get_x_and_y(get_predictor(last_set))

    if not X_test.empty:
        any_results_flag = True
        last_entry = last_set.iloc[-1:].iloc[0]

        prediction_output = multioutputregressor.predict(X_test)[0]
        close_value, high_value, low_value, open_value, volume_rel = prediction_output

        new_close = max(last_entry.close - close_value, 0.0)
        new_high = max(last_entry.high - high_value, 0.0)
        new_low = max(last_entry.low - low_value, 0.0)
        new_open = max(last_entry.open - open_value, 0.0)
        new_volume = max(last_entry.volume * volume_rel, 0.0)

        new_low, new_high = sorted([new_low, new_high])  # ensure low is smaller high
        new_low, new_close = sorted([new_low, new_close])  # ensure low is smaller high
        new_low, new_open = sorted([new_low, new_open])  # ensure low is smaller high

        new_open, new_high = sorted([new_open, new_high])  # ensure low is smaller high
        new_close, new_high = sorted([new_close, new_high])  # ensure low is smaller high
        new_spread = new_high - new_low

        last_set = last_set.append(pd.Series({
            "open": round(new_open, 6),
            "close": round(new_close, 6),
            "high": round(new_high, 6),
            "low": round(new_low, 6),
            "spread": round(new_spread, 6),
            "volume": round(new_volume)
        }, name=last_set.iloc[-1:].index.values[0] + 86400000), ignore_index=False)

if any_results_flag:
    last_set.iloc[(-1 * p):].to_csv("data/predict-low-high-" + symbol + ".csv", sep=",", header=True, decimal='.',
                                      float_format='%f')
