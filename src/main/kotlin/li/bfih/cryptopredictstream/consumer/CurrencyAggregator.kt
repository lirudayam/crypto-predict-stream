package li.bfih.cryptopredictstream.consumer

import li.bfih.cryptopredictstream.model.CurrencyEntry
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.java.tuple.Tuple2

class CurrencyAggregator : AggregateFunction<CurrencyEntry?, Tuple2<String, Long>, Tuple2<String, Long>> {

    override fun createAccumulator(): Tuple2<String, Long>? {
        return Tuple2("", 0L)
    }

    override fun add(value: CurrencyEntry?, p1: Tuple2<String, Long>?): Tuple2<String, Long>? {
        p1?.f0 = value?.symbol
        p1?.f1 = p1?.f1?.plus(1)
        return p1
    }

    override fun getResult(p0: Tuple2<String, Long>?): Tuple2<String, Long>? {
        return p0
    }

    override fun merge(a: Tuple2<String, Long>?, p1: Tuple2<String, Long>?): Tuple2<String, Long>? {
        return Tuple2(a?.f0 ?: "", a?.f1?.plus(p1?.f1!!) ?: 0)
    }
}