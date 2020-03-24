package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.anomaly.AnomalyOutput
import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.model.CurrencyEntry
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KFunction1

class RuleExecutor(private val dataSet: MutableIterable<CurrencyEntry?>?, private val property: KFunction1<CurrencyEntry, Float>, private val confidenceIntervalFigure: Int) {

    private var avg: Float = 0.0f
    private var derivation = 0.0f
    private var compare = 0.0f
    private var min = 0.0f
    private var max = 0.0f

    private fun calculateRatio(it: CurrencyEntry?, compare: CurrencyEntry?): Float {
        val v1 = it?.let { it1 -> property(it1) } ?: 1.0f
        val v2 = compare?.let { it1 -> property(it1) } ?: 0.0f
        if (v2 == 0.0f) {
            return 1.0f
        }
        return v1 / v2
    }

    private fun applyRule() : Boolean {
        if (dataSet != null && dataSet.count() > 2) {
            val lastEntry = dataSet.last()
            val kpiList = dataSet.filter { it?.date != lastEntry?.date }
            val n = kpiList.count()

            compare = calculateRatio(lastEntry, kpiList.last())
            var lastElm : CurrencyEntry? = null

            avg = (kpiList.map {
                var l = 1.0f
                if (lastElm != null) {
                    l = calculateRatio(it, lastElm)
                }
                lastElm = it
                l
            }.sum() / n)

            lastElm = null
            derivation = sqrt(kpiList.map{
                var l = 0.0f
                if (lastElm != null) {
                    l = (calculateRatio(it, lastElm) - avg).pow(2)
                }
                lastElm = it
                l
            }.sum() / n)

            // check if useful results
            if (avg == 1.0f && derivation == 0.0f) {
                return false
            }

            min = max(avg - confidenceIntervalFigure * derivation, 0.0f)
            max = avg + confidenceIntervalFigure * derivation

            return (compare < min) || (compare > max)
        }
        return false
    }

    fun executeRule(rule: Rule) : AnomalyOutput? {
        if (applyRule()) return dataSet?.last()?.date?.let { it -> AnomalyOutput(dataSet.last()?.symbol?.let { CryptoCurrency.getCurrency(it) }!!, it, rule.anomalyType.string, compare, min, max) }
        return null
    }


}