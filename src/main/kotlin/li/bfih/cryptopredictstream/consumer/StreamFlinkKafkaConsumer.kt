package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import li.bfih.cryptopredictstream.model.CurrencyEntryDeserializer
import li.bfih.cryptopredictstream.serialization.CryptoSerializationConfig
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object StreamFlinkKafkaConsumer {

    private val LOG: Logger = LoggerFactory.getLogger(StreamFlinkKafkaConsumer::class.java)
    private val OM = CryptoSerializationConfig.getMapper()

    fun startFlinkListening() {

        val see: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()

        val properties = Properties()
        properties.setProperty("bootstrap.servers", "localhost:9092")
        properties.setProperty("group.id", "group_id")

        val kafkaSource: FlinkKafkaConsumer<CurrencyEntry?> = FlinkKafkaConsumer(CryptoSerializationConfig.TOPIC, CurrencyEntryDeserializer(), properties)
        val stream: DataStreamSource<CurrencyEntry?>? = see.addSource(kafkaSource)
        //stream?.map(CurrencyOutputer())
        stream?.print()
        see.execute()
        //stream.print()

        /*val streamMap : SingleOutputStreamOperator<CurrencyEntry?> = stream.map { data ->
            return@map try {
                OM.readValue(data, CurrencyEntry::class.java)
            } catch (e: Exception) {
                LOG.info("exception reading data: $data")
                null
            }
        }.returns(TypeInformation.of(CurrencyEntry::class.java)).filter(Objects::nonNull)
        val customerPerCountryStream: KeyedStream<CurrencyEntry?, String> = streamMap.keyBy { it?.symbol ?: "" }

        val result: DataStream<Tuple2<String, Long>> = customerPerCountryStream.timeWindow(Time.seconds(5))
                .aggregate(CurrencyAggregator())

        result.print()

        see.execute("CustomerRegistrationApp")*/

    }

}