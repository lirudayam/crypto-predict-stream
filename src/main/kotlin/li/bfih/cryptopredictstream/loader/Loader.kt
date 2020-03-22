package li.bfih.cryptopredictstream.loader

import li.bfih.cryptopredictstream.currency.CryptoCurrencyRepository
import li.bfih.cryptopredictstream.serialization.CryptoSerializationConfig
import li.bfih.cryptopredictstream.websocket.handler.WebInterfaceMessageHandlerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.util.FileCopyUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.stream.Collectors

object Loader  {
    private var currentDate : LocalDate = LocalDate.now()
    private val logger: Logger = LoggerFactory.getLogger(Loader::class.java)

    private val mapper = CryptoSerializationConfig.getMapper()

    fun sendMessage(kafkaTemplate: KafkaTemplate<String, String?>) {
        val list = CryptoCurrencyRepository.getEntriesForDate(currentDate)
        list.forEach { entry -> kafkaTemplate.send(CryptoSerializationConfig.TOPIC, mapper.writeValueAsString(entry)) }
        currentDate = currentDate.plusDays(1)
        WebInterfaceMessageHandlerFactory.getMainInstance()?.sendSimulatedDate(currentDate)
    }

    fun startLoad() {
        addDataToRepos()
        currentDate = CryptoCurrencyRepository.minDate
        logger.info("Loading completed")
    }

    private fun addDataToRepos() {
        val cpr = ClassPathResource("crypto-markets.csv")
        try {
            val resource: InputStream = cpr.inputStream
            var i = 0
            BufferedReader(InputStreamReader(resource)).use { reader ->
                reader.lines().forEach{
                    if (i != 0) {
                        CryptoCurrencyRepository.addEntry(it)
                    }
                    i++
                }
            }
        } catch (e: IOException) {
            logger.error("Error reading file")
        }
    }
}