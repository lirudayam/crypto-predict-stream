package li.bfih.cryptopredictstream.websocket.handler

import li.bfih.cryptopredictstream.anomaly.AnomalyOutput
import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.messaging.MessagingException
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.LocalDate

@Component
class WebInterfaceMessageHandler : ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    lateinit var simpMessagingTemplate: SimpMessagingTemplate

    fun sendCurrencyEntry(currencyEntry: CurrencyEntry?) {
        try {
            if (currencyEntry != null) {
                simpMessagingTemplate.convertAndSend("/topic/currencyRaw", currencyEntry)
            }
        }
        catch (e: MessagingException) {
            print(e.failedMessage)
        }
    }

    fun sendSimulatedDate(date: LocalDate) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/currentDate", date)
        }
        catch (e: MessagingException) {
            print(e.failedMessage)
        }
    }

    fun sendAnomalyEntry(anomaly: AnomalyOutput?) {
        try {
            if (anomaly != null) {
                simpMessagingTemplate.convertAndSend("/topic/anomaly", anomaly)
            }
        }
        catch (e: MessagingException) {
            print(e.failedMessage)
        }
    }

    override fun onApplicationEvent(p0: SessionDisconnectEvent) {
        // do something
    }
}