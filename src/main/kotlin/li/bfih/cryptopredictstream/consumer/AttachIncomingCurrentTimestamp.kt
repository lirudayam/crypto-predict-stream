package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.api.common.functions.MapFunction
import java.util.*

class AttachIncomingCurrentTimestamp() : MapFunction<CurrencyEntry?, CurrencyEntry?> {

    override fun map(p0: CurrencyEntry?): CurrencyEntry? {
        p0?.dateAdded = Date()
        return p0
    }

}