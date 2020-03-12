package li.bfih.cryptopredictstream.websocket.handler

object WebInterfaceMessageHandlerFactory {

    var instance : WebInterfaceMessageHandler? = null
    fun getMainInstance(): WebInterfaceMessageHandler? {
        return instance
    }
}