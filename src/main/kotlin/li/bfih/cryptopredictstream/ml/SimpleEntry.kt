package li.bfih.cryptopredictstream.ml

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.io.Serializable

@JsonPropertyOrder("date", "open", "high", "low", "close", "volume", "spread")
class SimpleEntry (
    @JsonProperty("date") val date: Long,
    @JsonProperty("open") val open: Float,
    @JsonProperty("high") val high: Float,
    @JsonProperty("low") val low: Float,
    @JsonProperty("close") val close: Float,
    @JsonProperty("volume") val volume: Long,
    @JsonProperty("spread") val spread: Float
) : Serializable {
    @JsonIgnore
    override fun toString(): String {
        return "SimpleEntry(date=$date, open=$open, high=$high, low=$low, close=$close, volume=$volume, spread=$spread)"
    }
}