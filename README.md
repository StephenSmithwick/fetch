# Fetch

Is a simple restful http service which accepts a list of urls in at the 'jobs' endpoint
and will asynchronously work on the results which can be fetched from the 'results'
endpoint

## Getting Started

This is a Spring Boot application with an embedded Netty service.  The application
is written in Kotlin and built using gradle.

### Prerequisites

To build and run this application you will require:
- Java 1.8 - Kotlin targets jvm 1.8 bytecode
- Gradle


### Installing

To run immediately on your machine:
```
gradle clean bootRun
```
fetch will listen on port 8080 and responds on `/jobs` and `/results` endpoints

### Getting Started

To get started you can create a job using the test.data at root of the project:
```
curl http://localhost:8080/jobs --header "Content-Type: application/json" --data @test.data
```

The response will include a link to where you can query about the status of the job:
```
curl http://localhost:8080/{job.links.self} --header "Content-Type: application/json"
```

Eventually you will receive a response on the job endpoint which will include a link
to the results
```
curl http://localhost:8080/{job.links.result} --header "Content-Type: application/json"
```

## TODO

Areas where more work is necessary:
- Tests:
  - The fetch tests around `qantas.com` is not currently working as expected
  - The fetch tests should use an internal server to make the tests less brittle
  - Coverage is bit spotty and currently only focusses on the complicated bits
- The asynchronous coroutines from Kotlin were not quite working correctly within springboot.  So I settled on Springboot Aync annotations which are meain to be a less effecient. Further research to see if there is a workaround is required.
- Error Handling
  - Bad server requests are not well covered by tests and likely have some issues
  - We do not have any robust mechanisms to deal with issues that come up while running the job such as downstream issues.  It would be appropriate to consider a Netflix/Hystrix type solution here.
- Persistence
  - I chose JPA paired with an in memory H2 db for ease and expedience but given more time I'd explore 1 of 2 paths:
  1. Cleanup the move persistence out of the box so the processing can be stateless and scale easier.  We don't need to persist the links columns in both jobs and results and we could save potential table lock issues if we do so.
  2. Embrace the Document nature of the 2 natural object (Jobs and Results) and use a document store.

## Author

* **Stephen Smithwick** - stephen.smithwick [at] gmail.com
