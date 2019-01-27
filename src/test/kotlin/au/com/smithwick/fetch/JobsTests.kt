package au.com.smithwick.fetch

import io.kotlintest.shouldBe
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobsTests(
		@Autowired val restTemplate: TestRestTemplate,
		@Autowired val repository: JobsRepository
) {
    @Test
    fun `Assert a job generates links based based off job and results ids`() {
        val jobID = UUID.randomUUID()
        val resultID = UUID.randomUUID()

		val job = Job(id = jobID, urls = listOf("1", "2"), resultId = resultID)

        job.links shouldBe JobLinks(
             self = "/jobs/$jobID",
             result = "/results/$resultID"
        )
    }

    @Test
    fun `Assert a job does not generate a results link if results arent in yet`() {
        val jobID = UUID.randomUUID()

        val job = Job(id = jobID, urls = listOf("1", "2"), resultId = null)

        job.links shouldBe JobLinks(
                self = "/jobs/$jobID",
                result = null
        )
    }
}
