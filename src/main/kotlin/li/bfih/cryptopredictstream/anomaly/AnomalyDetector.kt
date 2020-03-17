package li.bfih.cryptopredictstream.anomaly

import li.bfih.cryptopredictstream.model.CurrencyEntry
import li.bfih.cryptopredictstream.rules.RuleFactory
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class AnomalyDetector : ProcessWindowFunction<CurrencyEntry?, String, String, TimeWindow>() {

    override fun process(currencyEntryId: String?, context: Context?, input: MutableIterable<CurrencyEntry?>?, out: Collector<String>?) {
        RuleFactory.applyRulesOnDataSet(input)
    }
}