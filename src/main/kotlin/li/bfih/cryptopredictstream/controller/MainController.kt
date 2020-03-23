package li.bfih.cryptopredictstream.controller

import li.bfih.cryptopredictstream.consumer.StreamFlinkKafkaConsumer
import li.bfih.cryptopredictstream.loader.Loader
import li.bfih.cryptopredictstream.ml.CurrencyConverter
import li.bfih.cryptopredictstream.websocket.handler.WebInterfaceMessageHandler
import li.bfih.cryptopredictstream.websocket.handler.WebInterfaceMessageHandlerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/stream"])
class MainController {
    private val logger: Logger = LoggerFactory.getLogger(MainController::class.java)

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String?>

    @Autowired
    private lateinit var webInterfaceMessageHandler: WebInterfaceMessageHandler

    var loadComplete = false

    @GetMapping(value = ["/trigger"])
    fun triggerLoad() {
        if (!loadComplete) {
            WebInterfaceMessageHandlerFactory.instance = webInterfaceMessageHandler

            logger.info("Clear files")
            CurrencyConverter.clearAllFiles()

            logger.info("Start loading")
            Loader.startLoad()

            logger.info("Load is completed -> Stream can be started")
            loadComplete = true

            StreamFlinkKafkaConsumer.startFlinkListening()
        }
    }

    @GetMapping(value = ["/manualNextDay"])
    fun simulateADay() {
        Loader.sendMessage(kafkaTemplate)
    }

    @GetMapping(value = ["/pauseSimulation"])
    fun pause() {
        loadComplete = false
    }

    @GetMapping(value = ["/resumeSimulation"])
    fun resume() {
        loadComplete = true
    }

    @Scheduled(fixedRate = 2000, initialDelay = 3000)
    @GetMapping(value = ["/cronJob"])
    fun simulateNextDay() {
        if (loadComplete) {
            WebInterfaceMessageHandlerFactory.instance = webInterfaceMessageHandler
            Loader.sendMessage(kafkaTemplate)
        }

    }
}