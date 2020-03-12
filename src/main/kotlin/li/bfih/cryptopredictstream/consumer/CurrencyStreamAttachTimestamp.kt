package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.api.common.functions.MapFunction

class CurrencyStreamAttachTimestamp : MapFunction<CurrencyEntry?, CurrencyEntry> {
    override fun map(p0: CurrencyEntry?): CurrencyEntry {
        p0?.timeStamp = System.currentTimeMillis()
        return p0 ?: CurrencyEntry("")
    }
}