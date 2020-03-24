package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.anomaly.AnomalyOutput
import li.bfih.cryptopredictstream.anomaly.AnomalyType
import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.springframework.web.client.RestTemplate

object RuleFactory {

    private const val uri = "http://localhost:8080/internal/anomaly"
    private const val ABNORMAL_SIGMA = 3
    private val ruleList = arrayOf(
            Rule(CurrencyEntry::getIntraDayMovement, AnomalyType.INTRADAY, ABNORMAL_SIGMA),
            Rule(CurrencyEntry::getFloatSpread, AnomalyType.SPREAD, ABNORMAL_SIGMA),
            Rule(CurrencyEntry::getFloatVolume, AnomalyType.VOLUME, ABNORMAL_SIGMA)
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