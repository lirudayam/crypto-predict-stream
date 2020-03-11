package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

class CurrencyStreamEntryTimeAssigner: BoundedOutOfOrdernessTimestampExtractor<CurrencyEntry>(Time.seconds(5)) {

    override fun extractTimestamp(entry: CurrencyEntry) = entry.date.time

}