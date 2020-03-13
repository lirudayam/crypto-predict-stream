package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.springframework.web.client.RestTemplate

object RuleFactory {

    private val uri = "http://localhost:8080/internal/anomaly"
    private val ruleList = arrayOf<Rule>(
            Rule(CurrencyEntry::getFloatClose,
                    "percentage", 2, "symbol moved more than 20% (date)"),
            Rule(CurrencyEntry::getFloatSpread,
                    "sigma", 3, "abnormal spread of symbol (date)"),
            Rule(CurrencyEntry::getFloatVolume,
                    "volume", 3, "abnormal spread of symbol (date)")
    )

    fun applyRulesOnDataSet(dataSet: MutableIterable<CurrencyEntry?>?) {
        val anomalyCollector = mutableListOf<String>()
        ruleList.forEach {
            val ruleExecutor = RuleExecutor(dataSet, it.property, it.anomalyType, it.confidenceIntervalFigure)
            ruleExecutor.executeRule(it)?.let { it1 -> anomalyCollector.add(it1) }
        }

        val restTemplate = RestTemplate()
        anomalyCollector.forEach {
            restTemplate.postForObject(uri, it, String::class.java)
        }
    }
}