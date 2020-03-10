package li.bfih.cryptopredictstream.service

import li.bfih.cryptopredictstream.currency.CurrencyEntry
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class StreamListenerService {
    private val logger: Logger = LoggerFactory.getLogger(Producer::class.java)

    @KafkaListener(topics = ["users"], groupId = "group_id")
    @Throws(IOException::class)
    fun consume(entry: CurrencyEntry?) {
        logger.info(String.format("New values for cryptocurrency %s received", entry?.symbol))
    }
}