package li.bfih.cryptopredictstream.stream

import com.github.signaflo.timeseries.TimePeriod
import com.github.signaflo.timeseries.TimeSeries
import com.github.signaflo.timeseries.forecast.Forecast
import com.github.signaflo.timeseries.model.arima.Arima
import com.github.signaflo.timeseries.model.arima.ArimaOrder
import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.service.StreamListenerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

object ARIMAModel {
    private val logger: Logger = LoggerFactory.getLogger(ARIMAModel::class.java)
    private val forecastRepository: MutableMap<String, Forecast> = mutableMapOf();

    @Async
    fun forecastData(currency: CryptoCurrency) {
        val entryList = StreamListenerRepository.getList(currency)
        val list = arrayListOf<Double>()
        entryList?.map { it -> list.add(it.open.toDouble()) }

        val data = TimeSeries.from(TimePeriod.oneDay(),
        entryList?.first()?.date?.toInstant()?.atOffset(ZoneOffset.UTC) ?: OffsetDateTime.of(2010, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC), *list.toDoubleArray())
        val modelOrder = ArimaOrder.order(2, 1, 0)
        val model = Arima.model(data, modelOrder)
        forecastRepository[currency.symbol] = model.forecast(1, 0.80)
    }

    fun compareData(symbol: String, value: Float, date: Date): Boolean {
        val forecast = forecastRepository[symbol]
        return if (forecast != null) {
            val offset = date.toInstant()?.atOffset(ZoneOffset.UTC) ?: OffsetDateTime.of(2010, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)
            logger.info(forecast.lowerPredictionInterval()?.at(offset).toString())
            logger.info(value.toString())
            logger.info(forecast.upperPredictionInterval()?.at(offset).toString())
            logger.info("---------------------------------")
            value > forecast.lowerPredictionInterval()?.at(offset)!! && value < forecast.upperPredictionInterval()?.at(offset)!!
        }
        else {
            true
        }
    }
}