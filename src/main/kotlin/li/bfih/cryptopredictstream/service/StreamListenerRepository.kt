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
    private val repository: MutableMap<String, MutableList<CurrencyEntry>> = mutableMapOf();

    fun addJumpStartEntry(entry: CurrencyEntry) {
        checkOfMap(entry.symbol)
        repository[entry.symbol]?.add(entry)
    }

    fun addEntry(entry: CurrencyEntry) {
        checkOfMap(entry.symbol)
        repository[entry.symbol]?.add(entry)
        logger.info("{} repo has {} entries", entry.symbol, repository[entry.symbol]?.size.toString())
        if (!ARIMAModel.compareData(entry.symbol, entry.low, entry.date)) {
            logger.error("ANOMALY detected for {} on {}", entry.symbol, entry.date.toString())
        }
        ARIMAModel.forecastData(CryptoCurrency.getCurrency(entry.symbol)!!)
    }

    fun getList(currency: CryptoCurrency): MutableList<CurrencyEntry>? {
        return repository[currency.symbol]
    }

    private fun checkOfMap(symbol: String) {
        if (!repository.containsKey(symbol)) {
            repository[symbol] = mutableListOf()
        }
    }

}