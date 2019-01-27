package au.com.smithwick.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID
import javax.persistence.*
import javax.persistence.FetchType.EAGER
import javax.transaction.Transactional

@Embeddable data class ResultLinks(
    val self: String,
    val job: String
)

@Entity
data class URLDetail(
        val url : String,
        val title : String?,
        val tlsAvailable : Boolean,
        val tlsOnly : Boolean
) {
    @Id val id: UUID = UUID.randomUUID()
}

@Entity
data class Result(
        @Id val id: UUID = UUID.randomUUID(),
        @OneToMany(fetch = EAGER) val results: List<URLDetail>,
        val jobId: UUID
) {
    @Embedded val links = ResultLinks(
            self = "/results/$id",
            job = "/jobs/$jobId")
}

@Transactional(Transactional.TxType.MANDATORY)
interface ResultsRepository : CrudRepository<Result, UUID>

@RestController @RequestMapping("/results") class ResultsController(
    @Autowired val repository: ResultsRepository
) {
    @GetMapping("{resultsID}")
    fun get(@PathVariable resultsID: UUID): Result {
        return repository.findByIdOrNull(resultsID)!!
    }

    @GetMapping
    fun all(): Iterable<Result> {
        return repository.findAll()
    }
}
