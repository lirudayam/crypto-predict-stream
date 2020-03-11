package li.bfih.cryptopredictstream.controller

import li.bfih.cryptopredictstream.consumer.StreamFlinkKafkaConsumer
import li.bfih.cryptopredictstream.loader.Loader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/kafka"])
class MainController {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String?>

    var loadComplete = false

    @GetMapping(value = ["/trigger"])
    fun triggerLoad() {
        logger.info("Start loading")
        Loader.startLoad()

        logger.info("Load is completed -> Stream can be started")
        loadComplete = true

        logger.info("Add entry into stream")
        Loader.sendMessage(kafkaTemplate)
        StreamFlinkKafkaConsumer.startFlinkListening()
    }

    @GetMapping(value = ["/nextDay"])
    fun simulateADay() {
        Loader.sendMessage(kafkaTemplate)
    }

    @Scheduled(fixedRate = 2000, initialDelay = 3000)
    @GetMapping(value = ["/send"])
    fun simulateNextDay() {
        if (loadComplete) {
            Loader.sendMessage(kafkaTemplate)
        }

    }
}