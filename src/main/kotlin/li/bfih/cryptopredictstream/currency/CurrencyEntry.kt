package li.bfih.cryptopredictstream.currency

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date

data class CurrencyEntry(
        @JsonProperty("symbol") var symbol: String,
        @JsonFormat
        (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") var date: Date,
        @JsonProperty("rankNow") var rankNow: Int,
        @JsonProperty("open") var open: Float,
        @JsonProperty("high") var high: Float,
        @JsonProperty("low") var low: Float,
        @JsonProperty("close") var close: Float,
        @JsonProperty("volume") var volume: Long,
        @JsonProperty("market") var market: Float,
        @JsonProperty("closeRatio") var closeRatio: Float,
        @JsonProperty("spread") var spread: Float
)