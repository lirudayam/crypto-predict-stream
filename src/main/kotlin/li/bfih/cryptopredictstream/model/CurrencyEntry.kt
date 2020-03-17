package li.bfih.cryptopredictstream.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import li.bfih.cryptopredictstream.currency.CryptoCurrency
import java.io.Serializable
import java.util.*

class CurrencyEntry(
        @JsonProperty("symbol") var symbol: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") var date: Date = Date(),
        @JsonProperty("rankNow") var rankNow: Int = 0,
        @JsonProperty("open") var open: Float = 0f,
        @JsonProperty("high") var high: Float = 0f,
        @JsonProperty("low") var low: Float = 0f,
        @JsonProperty("close") var close: Float = 0f,
        @JsonProperty("volume") var volume: Long = 0L,
        @JsonIgnore var market: Float = 0f,
        @JsonIgnore var closeRatio: Float = 0f,
        @JsonProperty("spread") var spread: Float = 0f,
        @JsonIgnore var timeStamp: Long = 0L
        ) : Serializable {
    companion object {
        private const val serialVersionUID = 20180617104400L
    }

    fun getFloatVolume() : Float {
        return volume.toFloat()
    }

    fun getFloatSpread() : Float {
        return spread
    }

    fun getSymbolName() : String? {
        return CryptoCurrency.getCurrency(symbol)?.currencyName
    }

    fun getIntraDayMovement(): Float {
        return high / low
    }


}