package au.com.smithwick.fetch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ElementCollection
import javax.persistence.Embedded
import javax.persistence.FetchType.EAGER
import javax.transaction.Transactional

data class JobRequest(val urls: List<String>)

@Embeddable data class JobLinks(
    val self: String,
    @JsonInclude(NON_NULL) val result: String?
)

@Entity
class Job(
    @Id val id: UUID = UUID.randomUUID(),
    @ElementCollection(fetch = EAGER) val urls: List<String>,
    @JsonInclude(NON_NULL) val resultId: UUID? = null
) {
    @Embedded val links = JobLinks(
            self = "/jobs/$id",
            result = if (resultId != null) "/results/$resultId" else null)
}

@Transactional(Transactional.TxType.MANDATORY)
interface JobsRepository : CrudRepository<Job, UUID>

@RestController
@RequestMapping("/jobs")
class JobsController(
    @Autowired val repository: JobsRepository,
    @Autowired val fetchService: FetchService
) {
    @PostMapping
    fun create(@RequestBody jobRequest: JobRequest): Job {
        val job = repository.save(Job(urls = jobRequest.urls))
        fetchService.start(job)
        return job
    }

    @GetMapping("{jobID}")
    fun get(@PathVariable jobID: UUID): Job {
        return repository.findByIdOrNull(jobID)!!
    }

    @GetMapping
    fun all(): Iterable<Job> {
        return repository.findAll()
    }
}