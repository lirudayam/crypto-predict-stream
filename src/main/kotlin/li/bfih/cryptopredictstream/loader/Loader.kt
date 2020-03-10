package li.bfih.cryptopredictstream.loader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import li.bfih.cryptopredictstream.currency.CryptoCurrencyRepository
import li.bfih.cryptopredictstream.currency.CurrencyEntry
import li.bfih.cryptopredictstream.serialization.SerializationConfig
import li.bfih.cryptopredictstream.service.StreamListenerRepository
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import java.io.File
import java.time.LocalDate

object Loader  {
    private var currentDate : LocalDate = LocalDate.now()
    private val logger: Logger = LoggerFactory.getLogger(Producer::class.java)
    private const val TOPIC = "users"
    private val mapper = SerializationConfig.getMapper()

    fun sendMessage(kafkaTemplate: KafkaTemplate<String, String?>) {
        val list = CryptoCurrencyRepository.getEntriesForDate(currentDate)
        list.forEach { entry -> kafkaTemplate.send(TOPIC, mapper.writeValueAsString(entry)) }
        currentDate = currentDate.plusDays(1)
        logger.info("Current date is {}", currentDate.toString())
    }

    fun startLoad() {
        addDataToRepos()
        currentDate = CryptoCurrencyRepository.minDate
        logger.info("Loading completed")

        makeJumpStart(50)
        logger.info("Jump start completed")
    }

    private fun makeJumpStart(days: Int) {
        for (i in 1..days) {
            val list = CryptoCurrencyRepository.getEntriesForDate(currentDate)
            list.forEach { entry -> StreamListenerRepository.addJumpStartEntry(entry) }
            currentDate = currentDate.plusDays(1)
            print(currentDate)
        }
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