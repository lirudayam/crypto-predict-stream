package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.anomaly.AnomalyOutput
import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.model.CurrencyEntry
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KFunction1

class RuleExecutor(private val dataSet: MutableIterable<CurrencyEntry?>?, private val property: KFunction1<CurrencyEntry, Float>, private val confidenceIntervalFigure: Int) {

    private var avg: Float = 0.0f
    private var derivation = 0.0f
    private var compare = 0.0f

    private fun applyRule() : Boolean {
        if (dataSet != null && dataSet.count() > 1) {
            val lastEntry = dataSet.last()
            val kpiList = dataSet.filter { it?.date != lastEntry?.date }
            val n = kpiList.count()

            compare = lastEntry?.let { property(it) } ?: 0.0f
            avg = kpiList.map {
                    it?.let { it1 -> property(it1) } ?: 0.0f
                }.sum() / n
            derivation = sqrt(kpiList.map{
                (it?.let { it1 -> property(it1).minus(avg) })?.pow(2)  ?: 0.0f
            }.sum() / n)

            return (compare > avg + confidenceIntervalFigure * derivation) || (compare < avg - confidenceIntervalFigure * derivation)
        }
        return false
    }

    fun executeRule(rule: Rule) : AnomalyOutput? {
        if (applyRule()) return dataSet?.last()?.date?.let { it -> AnomalyOutput(dataSet.last()?.symbol?.let { CryptoCurrency.getCurrency(it) }!!, it, rule.anomalyType.string, compare, avg - rule.abnormalSigma * derivation, avg + rule.abnormalSigma * derivation) }
        return null
    }


}