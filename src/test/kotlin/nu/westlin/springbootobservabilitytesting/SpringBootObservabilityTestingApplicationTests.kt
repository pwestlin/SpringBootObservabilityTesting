package nu.westlin.springbootobservabilitytesting

import io.micrometer.observation.tck.TestObservationRegistry
import io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat
import io.micrometer.tracing.test.simple.SimpleTracer
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(FooServiceObservabilityTest.TestObservationRegistryConfiguration::class)
class FooServiceObservabilityTest(
    @Autowired private val fooService: FooService,
    @Autowired private val observationRegistry: TestObservationRegistry,
    @Autowired private val tracer: SimpleTracer
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun `foo() is called three times`() {
        logger.info("observationRegistry = $observationRegistry")

        val numberOfCalls = 3
        repeat(numberOfCalls) {
            fooService.foo()
        }

        assertThat(observationRegistry)
            .hasNumberOfObservationsEqualTo(numberOfCalls)
            .hasObservationWithNameEqualTo("fooService")
            .that()
            .hasBeenStarted()
            .hasBeenStopped()

        io.micrometer.tracing.test.simple.TracerAssert.assertThat(tracer)
            .lastSpan()
            .hasNameEqualTo("foo-service#foo")
            .isEnded()
    }

    @TestConfiguration
    class TestObservationRegistryConfiguration {

        @Bean
        fun observationRegistry(): TestObservationRegistry = TestObservationRegistry.create()

        @Bean
        fun tracer(): SimpleTracer = SimpleTracer()
    }
}