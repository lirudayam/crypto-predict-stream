package li.bfih.cryptopredictstream.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.text.SimpleDateFormat
import java.util.*

object SerializationConfig {
    private val mapper = ObjectMapper().registerModule(KotlinModule())

    fun getMapper(): ObjectMapper {
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.timeZone = TimeZone.getTimeZone("UTC")
        mapper.dateFormat = df

        return mapper;
    }
}