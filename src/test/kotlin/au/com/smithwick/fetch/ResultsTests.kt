package au.com.smithwick.fetch

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResultsTests(@Autowired val restTemplate: TestRestTemplate) {
    @Test
    fun `Assert a Result generates links based based off job and results ids`() {
        val jobID = UUID.randomUUID()
        val resultID = UUID.randomUUID()
        val urlDetail = URLDetail("url", "title", true, true)

        val result = Result(id = resultID, results = listOf(urlDetail), jobId = jobID)

        result.links shouldBe ResultLinks(
                self = "/results/$resultID",
                job = "/jobs/$jobID"
        )
    }
}
