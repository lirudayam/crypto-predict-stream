package li.bfih.cryptopredictstream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAsync
class CryptoPredictStreamApplication

fun main(args: Array<String>) {
	runApplication<CryptoPredictStreamApplication>(*args)
}
