package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import kotlin.test.assertEquals

class TestGithub62 {
    @Test
    fun testAnonymousClassSerialization() {
        val externalValue = "ggg"

        val result = defaultMapper.writeValueAsString(object {
            val value = externalValue
        })

        assertEquals("""{"value":"ggg"}""", result)
    }
}
