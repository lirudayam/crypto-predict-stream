package li.bfih.cryptopredictstream.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.util.*

class DoubleDigitSerializer : JsonSerializer<Float?>() {
    @Throws(IOException::class)
    override fun serialize(value: Float?, generator: JsonGenerator, serializers: SerializerProvider?) {
        generator.writeNumber(String.format(Locale.US, "%.2f", value))
    }
}