package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import kotlin.test.assertEquals

class TestGithub270 {
    data class Wrapper(private val field: String) {
        val upper = field.uppercase()
        fun field() = field
        fun stillAField() = field
    }

    @Test
    fun testPublicFieldOverlappingFunction() {
        val json = defaultMapper.writeValueAsString(Wrapper("Hello"))
        assertEquals("""{"upper":"HELLO"}""", json)
    }
}
