package li.bfih.cryptopredictstream.controller

import li.bfih.cryptopredictstream.loader.Loader
import org.apache.kafka.clients.producer.Producer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/kafka"])
class MainController {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String?>

    var loadComplete = false

    @GetMapping(value = ["/trigger"])
    fun triggerLoad() {
        Loader.startLoad()
        loadComplete = true
    }

    @Scheduled(fixedRate = 1000, initialDelay = 3000)
    @GetMapping(value = ["/send"])
    fun simulateNextDay() {
        if (loadComplete) {
            logger.info("SEND msg")
            Loader.sendMessage(kafkaTemplate)
        }

    }
}