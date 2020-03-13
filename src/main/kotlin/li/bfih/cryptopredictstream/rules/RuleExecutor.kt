package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.model.CurrencyEntry
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KFunction1

class RuleExecutor(private val dataSet: MutableIterable<CurrencyEntry?>?, private val property: KFunction1<CurrencyEntry, Float>, private val anomalyType: String, private val confidenceIntervalFigure: Int) {

    private fun applyRule() : Boolean {
        if (dataSet != null && dataSet.count() > 1) {
            val lastEntry = dataSet.last()
            val kpiList = dataSet.filter { it?.date != lastEntry?.date }
            val n = kpiList.count()


            val compare = lastEntry?.let { property(it) } ?: 0.0f
            if (anomalyType == "sigma") {
                val avg = kpiList.map {
                    it?.let { it1 -> property(it1) } ?: 0.0f
                }.sum() / n
                val derivation = sqrt(kpiList.map{
                    (it?.let { it1 -> property(it1).minus(avg) })?.pow(2)  ?: 0.0f
                }.sum() / n)

                return (compare > avg + confidenceIntervalFigure * derivation) || (compare < avg - confidenceIntervalFigure * derivation)
            }
            else if (anomalyType == "percentage") {
                val previousVal = property(kpiList.last()!!)
                return (compare / previousVal > 1 + confidenceIntervalFigure / 100) || (compare / previousVal < 1 - confidenceIntervalFigure / 100)
            }
        }
        return false
    }

    fun executeRule(rule: Rule) : String? {
        if (applyRule()) {
            return rule.outputString.replace("symbol", dataSet?.last()?.symbol ?: "").replace("date", dataSet?.last()?.date.toString())
        }
        return null
    }


}