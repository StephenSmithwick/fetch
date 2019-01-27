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

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests(@Autowired val restTemplate: TestRestTemplate) {
    @Test
    fun `Assert a job is created with a link that can be used to retrieve the results`() {
        val urls = listOf("example.com")

        val createJobResponse = restTemplate.postForEntity("/jobs", JobRequest(urls = urls), Job::class.java)
        val jobUri = createJobResponse.body!!.links.self

        val resultUri = await untilNotNull {
            restTemplate.getForEntity(jobUri, Job::class.java).body!!.links.result
        }

        val resultsResponse = restTemplate.getForEntity(resultUri, Result::class.java)
        resultsResponse.body!!.results shouldBe listOf(
                URLDetail(url = "example.com", title = "Example Domain", tlsAvailable = true, tlsOnly = false)
        )
    }
}
