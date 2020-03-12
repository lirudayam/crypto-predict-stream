package li.bfih.cryptopredictstream.websocket.handler

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext

import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ApplicationContextHolder : ApplicationContextAware {
    @Throws(BeansException::class)
    override fun setApplicationContext(p0: ApplicationContext) {
        context = p0
    }

    companion object {
        private var context: ApplicationContext? = null
        fun getContext(): ApplicationContext? {
            return context
        }
    }
}