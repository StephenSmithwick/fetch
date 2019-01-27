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
- The asynchronous coroutines from Kotlin are not quite working correctly within springboot. Further research to see if there is a workaround is required.
- Error Handling
  - Bad server requests are not well covered by tests and likely have some issues
  - We do not have any robust mechanisms to deal with issues that come up while running the job such as downstream issues.  It would be appropriate to consider a Netflix/Hystrix type solution here.

## Author

* **Stephen Smithwick** - stephen.smithwick [at] gmail.com
