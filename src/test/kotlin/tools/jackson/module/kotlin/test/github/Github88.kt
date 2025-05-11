package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import kotlin.test.assertEquals

class TestGithub88 {
    class CloneableKotlinObj(val id: String) : Cloneable

    @Test
    fun shouldDeserializeSuccessfullyKotlinCloneableObject() {
        val result = defaultMapper.writeValueAsString(CloneableKotlinObj("123"))

        assertEquals("{\"id\":\"123\"}", result)
    }

    @Test
    fun shouldDeserializeSuccessfullyJavaCloneableObject() {
        val result = defaultMapper.writeValueAsString(CloneableJavaObj("123"))

        assertEquals("{\"id\":\"123\"}", result)
    }
}
