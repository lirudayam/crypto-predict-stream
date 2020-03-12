package li.bfih.cryptopredictstream.websocket.handler

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator

interface WebDataStreamForwarderInterface {
    fun forwardEntryStream(rawStream: SingleOutputStreamOperator<CurrencyEntry>)
    fun forwardAnomalyStream(averageStream: SingleOutputStreamOperator<String>?)
}