package li.bfih.cryptopredictstream.service

import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.currency.CurrencyEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object StreamListenerRepository {
    private val repository: MutableMap<CryptoCurrency, MutableList<CurrencyEntry>> = mutableMapOf();

    fun addEntry(entry: CurrencyEntry) {
        val currency = checkOfMap(entry.symbol)
        if (currency !== CryptoCurrency.EMPTY) {
            repository[currency]?.add(entry)
        }
    }

    private fun checkOfMap(symbol: String): CryptoCurrency {
        val key = CryptoCurrency.getCurrency(symbol) ?: CryptoCurrency.EMPTY
        if (!repository.containsKey(key)) {
            repository[key] = mutableListOf()
        }
        return key;
    }

}