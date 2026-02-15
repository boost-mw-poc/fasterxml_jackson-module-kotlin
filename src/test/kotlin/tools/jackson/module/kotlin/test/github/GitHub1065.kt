package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonInclude
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import tools.jackson.module.kotlin.jacksonMapperBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHub1065 {
    data class Vehicle(val id: VehicleId?)

    @JvmInline
    value class VehicleId(val value: String)

    @Test
    fun test() {
        val mapper = jacksonMapperBuilder()
            .apply {
                changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_EMPTY) }
            }.build()

        val dto = Vehicle(id = VehicleId("vehicle-1"))

        assertDoesNotThrow {
            assertEquals(
                """{"id":"vehicle-1"}""",
                mapper.writeValueAsString(dto)
            )
        }
    }
}
