package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.anomaly.AnomalyOutput
import li.bfih.cryptopredictstream.anomaly.AnomalyType
import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.springframework.web.client.RestTemplate

object RuleFactory {

    private const val uri = "http://localhost:8080/internal/anomaly"
    private val ruleList = arrayOf(
            Rule(CurrencyEntry::getIntraDayMovement, AnomalyType.INTRADAY, 3),
            Rule(CurrencyEntry::getFloatSpread, AnomalyType.SPREAD, 3),
            Rule(CurrencyEntry::getFloatVolume, AnomalyType.VOLUME, 3)
    )

    fun applyRulesOnDataSet(dataSet: MutableIterable<CurrencyEntry?>?) {
        val anomalyCollector = mutableListOf<AnomalyOutput>()
        ruleList.forEach {
            val ruleExecutor = RuleExecutor(dataSet, it.property, it.abnormalSigma)
            ruleExecutor.executeRule(it)?.let { it1 -> anomalyCollector.add(it1) }
        }

        val restTemplate = RestTemplate()
        anomalyCollector.forEach {
            restTemplate.postForObject(uri, it, String::class.java)
        }
    }
}