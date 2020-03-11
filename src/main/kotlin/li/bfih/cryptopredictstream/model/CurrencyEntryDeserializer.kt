package li.bfih.cryptopredictstream.model

import li.bfih.cryptopredictstream.serialization.CryptoSerializationConfig
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import java.io.IOException

class CurrencyEntryDeserializer : DeserializationSchema<CurrencyEntry?> {
    @Throws(IOException::class)
    override fun deserialize(bytes: ByteArray?): CurrencyEntry {
        return objectMapper.readValue(bytes, CurrencyEntry::class.java)
    }

    override fun isEndOfStream(inputMessage: CurrencyEntry?): Boolean {
        return false
    }

    companion object {
        var objectMapper = CryptoSerializationConfig.getMapper()
    }

    override fun getProducedType(): TypeInformation<CurrencyEntry?> {
        return TypeInformation.of(CurrencyEntry::class.java)
    }

}