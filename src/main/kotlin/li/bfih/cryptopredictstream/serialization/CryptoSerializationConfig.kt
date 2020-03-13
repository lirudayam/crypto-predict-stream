package li.bfih.cryptopredictstream.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.text.SimpleDateFormat
import java.util.*

object CryptoSerializationConfig {
    private val mapper = ObjectMapper().registerModule(KotlinModule())
    const val TOPIC = "users"

    fun getMapper(): ObjectMapper {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")
        mapper.dateFormat = df
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        return mapper
    }
}