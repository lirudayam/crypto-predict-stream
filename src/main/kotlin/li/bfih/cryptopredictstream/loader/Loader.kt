package li.bfih.cryptopredictstream.loader

import li.bfih.cryptopredictstream.currency.CryptoCurrencyRepository
import li.bfih.cryptopredictstream.serialization.CryptoSerializationConfig
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import java.io.File
import java.time.LocalDate

object Loader  {
    private var currentDate : LocalDate = LocalDate.now()
    private val logger: Logger = LoggerFactory.getLogger(Producer::class.java)

    private val mapper = CryptoSerializationConfig.getMapper()

    fun sendMessage(kafkaTemplate: KafkaTemplate<String, String?>) {
        val list = CryptoCurrencyRepository.getEntriesForDate(currentDate)
        list.forEach { entry -> kafkaTemplate.send(CryptoSerializationConfig.TOPIC, mapper.writeValueAsString(entry)) }
        currentDate = currentDate.plusDays(1)
        logger.info("Add to stream")
    }

    fun startLoad() {
        addDataToRepos()
        currentDate = CryptoCurrencyRepository.minDate
        logger.info("Loading completed")
    }

    private fun addDataToRepos() {
        val classloader = Thread.currentThread().contextClassLoader
        var i = 0
        File(classloader?.getResource("crypto-markets.csv")?.file ?: "").forEachLine {
            if (i != 0) {
                CryptoCurrencyRepository.addEntry(it)
            }
            i++
        }
    }
}