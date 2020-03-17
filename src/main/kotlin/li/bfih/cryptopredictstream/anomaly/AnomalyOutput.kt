package li.bfih.cryptopredictstream.anomaly

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import li.bfih.cryptopredictstream.currency.CryptoCurrency
import li.bfih.cryptopredictstream.util.DoubleDigitSerializer
import java.io.Serializable
import java.util.*

class AnomalyOutput(
        @JsonProperty("currency") var currency: CryptoCurrency,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") var date: Date = Date(),
        @JsonProperty("anomalyType") var anomalyType: String,
        @JsonProperty("isValue") @JsonSerialize(using = DoubleDigitSerializer::class) var isValue: Float,
        @JsonProperty("low") @JsonSerialize(using = DoubleDigitSerializer::class) var low: Float,
        @JsonProperty("up") @JsonSerialize(using = DoubleDigitSerializer::class) var up: Float
) : Serializable