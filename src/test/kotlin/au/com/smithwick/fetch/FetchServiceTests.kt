package au.com.smithwick.fetch

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FetchServiceTests(@Autowired val fetchService: FetchService) {
    @Test @Disabled
    fun `Correct details are fetched from a url which only server tls`() {
        fetchService.fetchResults("qantas.com").get() shouldBe URLDetail(
                url = "qantas.com",
                tlsOnly = true,
                tlsAvailable = true,
                title = "Fly with Australia’s most popular airline | Qantas AU"
        )
    }

    @Test
    fun `Correct details are fetched from a url which does not serve tls`() {
        fetchService.fetchResults("neverssl.com").get() shouldBe URLDetail(
                url = "neverssl.com",
                tlsOnly = false,
                tlsAvailable = false,
                title = "NeverSSL - helping you get online"
        )
    }

    @Test
    fun `Correct details are fetched from google`() {
        fetchService.fetchResults("google.com").get() shouldBe URLDetail(
                url = "google.com",
                tlsOnly = false,
                tlsAvailable = true,
                title = "Google"
        )
    }
}
