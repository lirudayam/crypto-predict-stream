package li.bfih.cryptopredictstream.websocket.controller

import li.bfih.cryptopredictstream.anomaly.AnomalyOutput
import li.bfih.cryptopredictstream.websocket.handler.WebInterfaceMessageHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/internal"])
class ForwardStreamController {

    @Autowired
    private lateinit var webInterfaceMessageHandler: WebInterfaceMessageHandler

    @RequestMapping(value = ["/anomaly"], method = [RequestMethod.POST])
    fun forwardAnomaly(@RequestBody message: AnomalyOutput?) {
        if (message != null) {
            webInterfaceMessageHandler.sendAnomalyEntry(message)
        }
    }
}