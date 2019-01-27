package au.com.smithwick.fetch

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import javax.transaction.Transactional


@Service
class FetchService(
    @Autowired val resultsRepository: ResultsRepository,
    @Autowired val jobRepository: JobsRepository
) {

    @Transactional
    fun start(job: Job) {
        GlobalScope.launch {
            val results = job.urls.map {
                async {
                    fetchResults(it)
                }
            }

            runBlocking {
                val result = resultsRepository.save(Result(
                        results = results.map { it.await() },
                        jobId = job.id))
                jobRepository.save(Job(id = job.id, urls = job.urls, resultId = result.id))
            }
        }
    }

    fun fetchResults(url : String) : URLDetail {

        val httpSuported = httpSuported(url)
        val tlsSupported = tlsSuported(url)
        val title = fetchTitle(url)

        return URLDetail(
                url = url,
                tlsOnly = tlsSupported && !httpSuported,
                tlsAvailable = tlsSupported,
                title = title
        )
    }

    fun httpSuported(url : String) : Boolean {
        return try {
            Jsoup.connect("http://$url").header("Accept", "text/html").execute().url().protocol == "http"
        } catch (e : Exception) {
            false
        }
    }

    fun tlsSuported(url : String) : Boolean {
        return try {
            Jsoup.connect("https://$url").header("Accept", "text/html").execute().url().protocol == "https"
        } catch (e : Exception) {
            false
        }
    }

    fun fetchTitle(url : String) : String? {
        return try {
            Jsoup.connect("http://$url").header("Accept", "text/html").get().title()
        } catch (e : Exception) {
            try {
                Jsoup.connect("https://$url").header("Accept", "text/html").get().title()
            } catch (e : Exception) {
                null
            }
        }
    }


}


