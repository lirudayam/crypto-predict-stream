package li.bfih.cryptopredictstream.currency

enum class CryptoCurrency (
    val slug : String = "",
    val symbol: String = "",
    val currencyName: String = ""
) {
    BITCOIN("bitcoin" ,"BTC","Bitcoin"),
    BITCOIN_CASH("bitcoin-cash","BCH","Bitcoin Cash"),
    BITCOIN_GOLD("bitcoin-gold","BTG","Bitcoin Gold"),
    LITECOIN("litecoin","LTC","Litecoin"),
    ETHEREUM("ethereum-classic","ETC","Ethereum Classic"),
    RIPPLE("ripple","XRP","XRP"),
    CARDANO("cardano","ADA","Cardano"),
    STELLAR("stellar","XLM","Stellar"),

    EMPTY("", "", "");

    companion object {
        fun getCurrency(value: String): CryptoCurrency? = values().find { it.symbol == value }
    }
}