package li.bfih.cryptopredictstream.ml

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.springframework.scheduling.annotation.Async

object CurrencyConverter {
    private val initCurrencies : MutableList<String> = mutableListOf()

    private fun getSimpleEntries(iterator: MutableIterable<CurrencyEntry?>?): MutableList<SimpleEntry> {
        val list = mutableListOf<SimpleEntry>()
        iterator?.forEach{
            if (it != null) {
                list.add(SimpleEntry(it.date.time, it.open, it.high, it.low, it.close, it.volume, it.spread))
            }
        }
        return list
    }

    private fun fileExists(symbol: String?): Boolean {
        return initCurrencies.indexOf(symbol) == -1
    }

    @Async
    private fun callPythonModelTraining(symbol: String?) {
        PythonServer.trainModel(symbol)
    }

    @Async
    fun clearAllFiles() {
        PythonServer.cleanModels()
    }

    fun writeCSV(iterator: MutableIterable<CurrencyEntry?>?) {
        val csvMapper = CsvMapper()
        var csvSchema: CsvSchema = csvMapper.schemaFor(SimpleEntry::class.java)

        val symbol = iterator?.first()?.symbol ?: ""
        csvSchema = if (fileExists(symbol)) {
            csvSchema.withHeader()
        } else {
            csvSchema.withoutHeader()
        }
        var csvWriter = csvMapper.writer(csvSchema)

        val list = getSimpleEntries(iterator)
        list.forEach {
            PythonServer.saveData(csvWriter.writeValueAsString(it), symbol)
            if (fileExists(symbol)) {
                initCurrencies.add(symbol)
                csvWriter = csvMapper.writer(csvSchema.withoutHeader())
            }

        }

        callPythonModelTraining(symbol)
    }
}