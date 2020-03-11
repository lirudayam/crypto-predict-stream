package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.streaming.api.functions.windowing.WindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import kotlin.math.pow
import kotlin.math.sqrt

class CurrencyAggregator : WindowFunction<CurrencyEntry?, String, String, TimeWindow> {
    /**
     * apply() is invoked once for each window.
     *
     * @param sensorId the key (sensorId) of the window
     * @param window meta data for the window
     * @param input an iterable over the collected sensor readings that were assigned to the window
     * @param out a collector to emit results from the function
     */
    override fun apply(
            currencyEntryId: String,
            window: TimeWindow,
            input: MutableIterable<CurrencyEntry?>,
            out: Collector<String>
    ) {
        var cnt = 0
        var sumSpread = 0.0
        var sumVolume = 0.0
        var lastEntry : CurrencyEntry? = null
        var currentEntry : CurrencyEntry? = null
        val abnormalFactorSigma = 2

        val size = input.count()
        for (r in input) {
            if (size != cnt - 1) {
                sumSpread += r?.spread ?: 0.0f
                sumVolume += r?.volume ?: 0
                lastEntry = r!!
                cnt++
            }
            else {
                currentEntry = r!!
            }
        }
        val avgSpread = sumSpread / cnt
        val avgVolume = sumVolume / cnt

        var sumVarianceSpread = 0.0
        var sumVarianceVolume = 0.0
        var lnt = 0
        for (r in input) {
            if (size != lnt - 1) {
                sumVarianceSpread += (r?.spread?.minus(avgSpread))!!.pow(2)
                sumVarianceVolume += (r.spread.minus(avgSpread)).pow(2)
                lnt++
            }
        }

        val derivationSpread = sqrt(sumVarianceSpread / lnt)
        val derivationVolume = sqrt(sumVarianceVolume / lnt)

        if (currentEntry?.volume!! > avgVolume + abnormalFactorSigma * derivationVolume) {
            out.collect("abnormal high transaction volume of ${currentEntry.symbol} at ${currentEntry.date}")
        }
        if (currentEntry.volume < avgVolume - abnormalFactorSigma * derivationVolume) {
            out.collect("abnormal low transaction volume of ${currentEntry.symbol} at ${currentEntry.date}")
        }
        if (currentEntry.spread > avgSpread + abnormalFactorSigma * derivationSpread) {
            out.collect("abnormal high spread of ${currentEntry.symbol} at ${currentEntry.date}")
        }
        if (currentEntry.spread < avgSpread - abnormalFactorSigma * derivationSpread) {
            out.collect("abnormal low spread of ${currentEntry.symbol} at ${currentEntry.date}")
        }
        if (currentEntry.close > lastEntry?.close!! * 1.1) {
            out.collect("more than 10% up day-over-day of ${currentEntry.symbol} at ${currentEntry.date}")
        }
        if (currentEntry.close < lastEntry.close * 0.9) {
            out.collect("more than 10% down day-over-day of ${currentEntry.symbol} at ${currentEntry.date}")
        }
    }
}