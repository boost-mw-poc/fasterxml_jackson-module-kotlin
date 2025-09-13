package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.assertThrows
import tools.jackson.databind.exc.InvalidNullException
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.Test

class GitHub976 {
    data class PrimitiveList(val list: List<Int>)

    @Test
    fun newStrictNullChecksRegression() {
        val om = jacksonObjectMapper {
            enable(KotlinFeature.StrictNullChecks)
        }
        assertThrows<InvalidNullException> {
            om.readValue("""{"list": [""] }""".toByteArray(), PrimitiveList::class.java)
        }
    }
}
