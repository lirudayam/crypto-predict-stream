package li.bfih.cryptopredictstream.anomaly

enum class AnomalyType (val string : String = "")
{
    INTRADAY("intraday movement"),
    VOLUME("volume"),
    SPREAD("spread")
}