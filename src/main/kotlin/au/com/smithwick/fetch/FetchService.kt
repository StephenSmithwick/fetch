package au.com.smithwick.fetch

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import java.lang.Exception
import javax.transaction.Transactional

@Service
@EnableAsync
open class FetchJobService(
    @Autowired val resultsRepository: ResultsRepository,
    @Autowired val jobRepository: JobsRepository,
    @Autowired val fetchService: FetchService
) {
    @Transactional
    @Async
    fun start(job: Job) {
        val results = job.urls.map { fetchService.fetchResults(it) }
        val result = resultsRepository.save(Result(
                results = results.map { it.get() },
                jobId = job.id))
        jobRepository.save(Job(id = job.id, urls = job.urls, resultId = result.id))
    }
}

@Service
@EnableAsync
open class FetchService(
    @Autowired val urlDetailRepository: URLDetailRepository
) {
    @Async
    fun fetchResults(url: String): ListenableFuture<URLDetail> {
        val httpSuported = httpSuported(url)
        val tlsSupported = tlsSuported(url)
        val title = fetchTitle(url)

        return return AsyncResult.forValue(urlDetailRepository.save(URLDetail(
                url = url,
                tlsOnly = tlsSupported && !httpSuported,
                tlsAvailable = tlsSupported,
                title = title
        )))
    }

    fun httpSuported(url: String): Boolean {
        return try {
            connectAcceptHTML("http://$url").execute().url().protocol == "http"
        } catch (e: Exception) {
            false
        }
    }

    fun tlsSuported(url: String): Boolean {
        return try {
           connectAcceptHTML("https://$url").execute().url().protocol == "https"
        } catch (e: Exception) {
            false
        }
    }

    fun fetchTitle(url: String): String? {
        return try {
            connectAcceptHTML("http://$url").get().title()
        } catch (e: Exception) {
            try {
                connectAcceptHTML("https://$url").get().title()
            } catch (e: Exception) {
                null
            }
        }
    }

    fun connectAcceptHTML(url: String) = Jsoup.connect(url).header("Accept", "text/html")
}
