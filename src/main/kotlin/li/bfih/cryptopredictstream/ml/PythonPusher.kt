package li.bfih.cryptopredictstream.ml

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class PythonPusher : ProcessWindowFunction<CurrencyEntry?, String, String, TimeWindow>() {

    override fun process(currencyEntryId: String?, context: Context?, input: MutableIterable<CurrencyEntry?>?, out: Collector<String>?) {
        CurrencyConverter.writeCSV(input)
    }
}