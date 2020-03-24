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

    private fun applyRule() : Boolean {
        if (dataSet != null && dataSet.count() > 1) {
            val lastEntry = dataSet.last()
            val kpiList = dataSet.filter { it?.date != lastEntry?.date }
            val n = kpiList.count()

            compare = lastEntry?.let { property(it) / kpiList[n - 2]?.let { it1 -> property(it1) }!! } ?: 1.0f
            avg = (kpiList.mapIndexed { index, currencyEntry ->
                if (index > 0) {
                    currencyEntry?.let { property(it) }?.div(kpiList[index - 1]?.let { property(it) }!!)
                }
                1.0
            }.sum() / n).toFloat()
            derivation = sqrt(kpiList.mapIndexed { index, currencyEntry ->
                if (index > 0) {
                    (currencyEntry?.let { property(it) }?.div(kpiList[index - 1]?.let { property(it) }!!)?.minus(avg))?.pow(2)
                }
                0.0
            }.sum() / n).toFloat()

            /*compare = lastEntry?.let { property(it) } ?: 0.0f
            avg = kpiList.map {
                    it?.let { it1 -> property(it1) } ?: 0.0f
                }.sum() / n
            derivation = sqrt(kpiList.map{
                (it?.let { it1 -> property(it1).minus(avg) })?.pow(2)  ?: 0.0f
            }.sum() / n)*/

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