package nu.westlin.springbootobservabilitytesting

import io.micrometer.observation.annotation.Observed
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SpringBootObservabilityTestingApplication

fun main(args: Array<String>) {
    runApplication<SpringBootObservabilityTestingApplication>(*args)
}

@RestController
@RequestMapping("/")
class FooController(
    private val fooService: FooService
) {

    @GetMapping("/foo")
    fun foo(): String = fooService.foo()
}

@Service
@Observed(name = "fooService")
class FooService {
    fun foo(): String = "foo"
}