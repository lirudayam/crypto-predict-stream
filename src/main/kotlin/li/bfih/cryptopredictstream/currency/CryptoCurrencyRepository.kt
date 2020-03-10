package li.bfih.cryptopredictstream.currency

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object CryptoCurrencyRepository {
    private val repository: MutableMap<CryptoCurrency, MutableList<CurrencyEntry>> = mutableMapOf();
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)

    var minDate: LocalDate = LocalDate.now()

    fun addEntry(csv_line: String) {
        val lineParts = csv_line.split(",")
        val currency = checkOfMap(lineParts[1])
        if (currency !== CryptoCurrency.EMPTY) {
            val date = LocalDate.parse(lineParts[3], formatter)
            val formatDate = java.util.Date.from(date.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            val entry = CurrencyEntry(lineParts[1], formatDate, Integer.parseInt(lineParts[4]),
                    lineParts[5].toFloat(), lineParts[6].toFloat(), lineParts[7].toFloat(), lineParts[8].toFloat(),
                    lineParts[9].toDouble().toLong(), lineParts[10].toFloat(), lineParts[11].toFloat(), lineParts[12].toFloat())
            repository[currency]?.add(entry)
            if (date.isBefore(minDate)) {
                minDate = date
            }
        }
    }

    fun getEntriesForDate(localDate: LocalDate): MutableList<CurrencyEntry> {
        val entries = mutableListOf<CurrencyEntry>()
        val date = java.sql.Date.valueOf(localDate)
        repository.forEach { (_, v) ->
            val entry = v.singleOrNull { s -> s.date == date }
            if (entry != null) {
                entries.add(entry)
            }
        }
        return entries
    }

    private fun checkOfMap(symbol: String): CryptoCurrency {
        val key = CryptoCurrency.getCurrency(symbol) ?: CryptoCurrency.EMPTY
        if (!repository.containsKey(key)) {
            repository[key] = mutableListOf()
        }
        return key;
    }

}