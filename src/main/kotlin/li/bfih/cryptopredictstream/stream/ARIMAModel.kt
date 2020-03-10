package li.bfih.cryptopredictstream.stream

import com.github.signaflo.timeseries.TimePeriod
import com.github.signaflo.timeseries.TimeSeries
import com.github.signaflo.timeseries.TimeUnit
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

    //@Async
    fun forecastData(currency: CryptoCurrency) {
        val entryList = StreamListenerRepository.getList(currency)
        val list = arrayListOf<Double>()
        entryList?.map { it -> list.add(it.open.toDouble()) }

        val data = TimeSeries.from(TimePeriod.oneDay(), getOffset(entryList?.first()?.date), *list.toDoubleArray())
        val modelOrder = ArimaOrder.order(8, 8, 1)
        val model = Arima.model(data, modelOrder)
        forecastRepository[currency.symbol] = model.forecast(2, 0.80)
    }

    fun compareData(symbol: String, value: Float, date: Date): Boolean {
        val forecast = forecastRepository[symbol]
        return if (forecast != null) {
            val offset = getOffset(date)
            value > forecast.lowerPredictionInterval()?.at(offset)!! && value < forecast.upperPredictionInterval()?.at(offset)!!
        }
        else {
            true
        }
    }

    private fun getOffset(date: Date?) : OffsetDateTime {
        val d = date?.toInstant()?.atOffset(ZoneOffset.UTC)
        var o = OffsetDateTime.of(d?.year ?: 2010, d?.monthValue ?: 1, d?.dayOfMonth ?: 1, 0, 0, 0, 0, ZoneOffset.UTC)
        return o
        //return date?.toInstant()?.atOffset(ZoneOffset.UTC) ?: OffsetDateTime.of(2010, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)
    }
}