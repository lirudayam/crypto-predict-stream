package li.bfih.cryptopredictstream.ml

import org.springframework.web.client.RestTemplate

object PythonServer {

    //@Value( "#{mlpython.mlmodel.server.url}" )
    private var uri : String = "http://localhost:4444/"

    private val restTemplate = RestTemplate()

    fun trainModel(symbol: String?) {
        restTemplate.getForObject(uri + "train/" + symbol, String::class.java)
    }

    fun cleanModels() {
        restTemplate.getForObject(uri + "cleanup/", String::class.java)
    }

    fun saveData(payload: String, symbol: String?) {
        restTemplate.postForObject(uri + symbol, payload, String::class.java)
    }
}