package com.fasterxml.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.KotlinInvalidNullException
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TestGithub168 {
    @Suppress("UNUSED_PARAMETER")
    class TestClass(@JsonProperty(value = "foo", required = true) foo: String?, val baz: String)

    @Test
    fun testIfRequiredIsReallyRequiredWhenNullUsed() {
        val obj = defaultMapper.readValue<TestClass>("""{"foo":null,"baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenAbsent() {
        assertThrows<KotlinInvalidNullException> {
            val obj = defaultMapper.readValue<TestClass>("""{"baz":"whatever"}""")
            assertEquals("whatever", obj.baz)
        }
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenValuePresent() {
        val obj = defaultMapper.readValue<TestClass>("""{"foo":"yay!","baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }
}
