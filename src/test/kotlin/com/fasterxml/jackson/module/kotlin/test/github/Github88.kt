package com.fasterxml.jackson.module.kotlin.test.github

import com.fasterxml.jackson.module.kotlin.defaultMapper
import org.junit.jupiter.api.Test
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
