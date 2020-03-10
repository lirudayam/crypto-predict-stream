package li.bfih.cryptopredictstream.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import li.bfih.cryptopredictstream.currency.CurrencyEntry
import li.bfih.cryptopredictstream.serialization.SerializationConfig
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class StreamListenerService {
    private val logger: Logger = LoggerFactory.getLogger(Producer::class.java)
    private val mapper = SerializationConfig.getMapper()

    @KafkaListener(topics = ["users"], groupId = "group_id")
    @Throws(IOException::class)
    fun consume(entry: String?) {
        if (entry != null) {
            val currencyEntry = mapper.readValue(entry, CurrencyEntry::class.java)
            StreamListenerRepository.addEntry(currencyEntry)
        }
    }
}