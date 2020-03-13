package li.bfih.cryptopredictstream.rules

import li.bfih.cryptopredictstream.model.CurrencyEntry
import kotlin.reflect.KFunction1

data class Rule(val property: KFunction1<CurrencyEntry, Float>,
                val anomalyType: String,
                val confidenceIntervalFigure: Int,
                val outputString: String)