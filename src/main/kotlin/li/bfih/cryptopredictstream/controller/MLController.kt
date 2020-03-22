package li.bfih.cryptopredictstream.controller

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest

@RestController
class MLController {

    private val server = "localhost"
    private val port = 4444
    private val restTemplate = RestTemplate()

    @RequestMapping("/ml/**")
    @ResponseBody
    //@Throws(URISyntaxException::class)
    fun mirrorRest(@RequestBody body: String?, method: HttpMethod, request: HttpServletRequest): String? {

        val uri = URI("http", null, server, port, request.requestURI.removePrefix("/ml"), request.queryString, null)
        return try {
            val responseEntity: ResponseEntity<String> = restTemplate.exchange(uri, method, body?.let { HttpEntity(it) }, String::class.java)
            responseEntity.body
        }
        catch (e: Exception) {
            "" // catch the socket exception with end of file
        }
    }

}