package li.bfih.cryptopredictstream.service

import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.currency.CurrencyEntry
import li.bfih.cryptopredictstream.stream.ARIMAModel
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object StreamListenerRepository {
    private val logger: Logger = LoggerFactory.getLogger(StreamListenerRepository::class.java)
    private val repository: MutableMap<CryptoCurrency, MutableList<CurrencyEntry>> = mutableMapOf();

    fun addEntry(entry: CurrencyEntry) {
        val currency = checkOfMap(entry.symbol)
        if (currency !== CryptoCurrency.EMPTY) {
            repository[currency]?.add(entry)
            if (!ARIMAModel.compareData(currency.symbol, entry.low)) {
                logger.error("ANOMALY detected")
                logger.error(entry.date.toString())
            }
            ARIMAModel.forecastData(currency)
        }
    }

    fun getList(currency: CryptoCurrency): MutableList<CurrencyEntry>? {
        return repository[currency]
    }

    private fun checkOfMap(symbol: String): CryptoCurrency {
        val key = CryptoCurrency.getCurrency(symbol) ?: CryptoCurrency.EMPTY
        if (!repository.containsKey(key)) {
            repository[key] = mutableListOf()
        }
        return key;
    }

}