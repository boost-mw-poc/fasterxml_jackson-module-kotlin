package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JacksonInject
import org.junit.jupiter.api.Test
import tools.jackson.databind.InjectableValues
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.util.UUID
import kotlin.test.assertEquals

class TestGithub101_JacksonInjectTest {
    @Test
    fun `JacksonInject-annotated parameters are populated when constructing Kotlin data classes`() {
        val contextualValue = UUID.randomUUID()
        val reader = jacksonObjectMapper()
            .readerFor(SomeDatum::class.java)
            .with(InjectableValues.Std(mapOf("context" to contextualValue)))
        assertEquals(
            SomeDatum("test", contextualValue),
            reader.readValue("""{ "value": "test" }""")
        )
    }

    data class SomeDatum(val value: String, @JacksonInject("context") val contextualValue: UUID)
}
