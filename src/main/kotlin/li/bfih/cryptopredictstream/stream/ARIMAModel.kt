package li.bfih.cryptopredictstream.stream

import com.github.signaflo.timeseries.TimeSeries
import com.github.signaflo.timeseries.forecast.Forecast
import com.github.signaflo.timeseries.model.arima.Arima
import com.github.signaflo.timeseries.model.arima.ArimaOrder
import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.currency.CurrencyEntry
import li.bfih.cryptopredictstream.service.StreamListenerRepository
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import java.time.OffsetDateTime

object ARIMAModel {
    private val logger: Logger = LoggerFactory.getLogger(ARIMAModel::class.java)
    private val forecastRepository: MutableMap<String, Forecast> = mutableMapOf();

    @Async
    fun forecastData(currency: CryptoCurrency) {
        val entryList = StreamListenerRepository.getList(currency)
        val list = DoubleArray(entryList?.size ?: 0)
        entryList?.map { it -> list[list.size] = it.open.toDouble() }
        val data = TimeSeries.from(OffsetDateTime.now(), *list)
        val modelOrder = ArimaOrder.order(0, 1, 1, 0, 0, 0)
        val model = Arima.model(data, modelOrder)
        forecastRepository[currency.symbol] = model.forecast(1, 0.95)
    }

    fun compareData(symbol: String, value: Float): Boolean {
        val forecast = forecastRepository[symbol]
        return if (forecast != null) {
            value > forecast.lowerPredictionInterval()?.mean()!! && value < forecast.upperPredictionInterval()?.mean()!!
        }
        else {
            true
        }
    }
}