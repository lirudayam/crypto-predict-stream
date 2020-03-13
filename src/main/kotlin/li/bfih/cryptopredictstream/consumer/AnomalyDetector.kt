package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import li.bfih.cryptopredictstream.rules.RuleFactory
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class AnomalyDetector : ProcessWindowFunction<CurrencyEntry?, String, String, TimeWindow>() {

    private val uri = "http://localhost:8080/internal/anomaly"

    override fun process(currencyEntryId: String?, context: Context?, input: MutableIterable<CurrencyEntry?>?, out: Collector<String>?) {
        RuleFactory.applyRulesOnDataSet(input)
        /*var cnt = 0
        var sumSpread = 0.0
        var sumVolume = 0.0
        var lastEntry : CurrencyEntry? = input?.first()
        val currentEntry : CurrencyEntry? = input?.last()
        val abnormalFactorSigma = 3

        val size = input?.count()

        if (size != 0 && size != null && out != null && currentEntry != null) {
            for (r in input) {
                if (cnt != size - 1) {
                    sumSpread += r?.spread ?: 0.0f
                    sumVolume += r?.volume ?: 0
                    lastEntry = r
                    cnt++
                }
            }
            val avgSpread = sumSpread / cnt
            val avgVolume = sumVolume / cnt

            var sumVarianceSpread = 0.0
            var sumVarianceVolume = 0.0
            var lnt = 0
            for (r in input) {
                if (lnt != size - 1) {
                    sumVarianceSpread += (r?.spread?.minus(avgSpread))!!.pow(2)
                    sumVarianceVolume += (r.spread.minus(avgSpread)).pow(2)
                    lnt++
                }
            }

            val derivationSpread = sqrt(sumVarianceSpread / lnt)
            val derivationVolume = sqrt(sumVarianceVolume / lnt)

            val anomalyCollector = mutableListOf<String>()

            if (currentEntry.volume > avgVolume + abnormalFactorSigma * derivationVolume) {
                anomalyCollector.add("abnormal high transaction volume of ${currentEntry.symbol} at ${currentEntry.date}")
            }
            if (currentEntry.volume < avgVolume - abnormalFactorSigma * derivationVolume) {
                anomalyCollector.add("abnormal low transaction volume of ${currentEntry.symbol} at ${currentEntry.date}")
            }
            if (currentEntry.spread > avgSpread + abnormalFactorSigma * derivationSpread) {
                anomalyCollector.add("abnormal high spread of ${currentEntry.symbol} at ${currentEntry.date}")
            }
            if (currentEntry.spread < avgSpread - abnormalFactorSigma * derivationSpread) {
                anomalyCollector.add("abnormal low spread of ${currentEntry.symbol} at ${currentEntry.date}")
            }

            val lastClose = lastEntry?.close ?: currentEntry.close
            val delta = 15 // percentage points
            if ((currentEntry.close / lastClose) > 1 + delta / 100) {
                anomalyCollector.add("${currentEntry.symbol} raised more than ${delta}% (${currentEntry.date})")
            }
            if ((currentEntry.close / lastClose) < 1 - delta / 100) {
                anomalyCollector.add("${currentEntry.symbol} dropped more than ${delta}% (${currentEntry.date})")
            }

            val restTemplate = RestTemplate()
            anomalyCollector.forEach {
                restTemplate.postForObject(uri, it, String::class.java)
            }
        }*/
    }
}