package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import li.bfih.cryptopredictstream.model.CurrencyEntryDeserializer
import li.bfih.cryptopredictstream.serialization.CryptoSerializationConfig
import li.bfih.cryptopredictstream.websocket.handler.WebInterfaceMessageHandlerFactory
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStreamUtils
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object StreamFlinkKafkaConsumer {

    private val logger: Logger = LoggerFactory.getLogger(StreamFlinkKafkaConsumer::class.java)

    fun startFlinkListening() {

        logger.info("Flink listening started")
        val see: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()
        see.streamTimeCharacteristic = TimeCharacteristic.EventTime
        see.parallelism = 16

        val properties = Properties()
        properties.setProperty("bootstrap.servers", "localhost:9092")
        properties.setProperty("group.id", "group_id")

        val kafkaSource: FlinkKafkaConsumer<CurrencyEntry?> = FlinkKafkaConsumer(CryptoSerializationConfig.TOPIC, CurrencyEntryDeserializer(), properties)
        val rawStream = see.addSource(kafkaSource).map(CurrencyStreamAttachTimestamp()).assignTimestampsAndWatermarks(CurrencyStreamEntryTimeAssigner())

        val averageStream = rawStream?.keyBy(KeySelector<CurrencyEntry?, String> {
            it?.symbol!!
        })?.window(TumblingEventTimeWindows.of(Time.seconds(8), Time.seconds(1)))?.process(AnomalyDetector())

        // for web stream
        DataStreamUtils.collect(rawStream).iterator().forEach {
            WebInterfaceMessageHandlerFactory.instance?.sendCurrencyEntry(it)
        }

        see.execute()
    }

}