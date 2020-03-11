package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.api.common.functions.MapFunction

class CurrencyOutputer : MapFunction<CurrencyEntry?, String?> {
    override fun map(p0: CurrencyEntry?): String? {
        return p0?.symbol
    }

}