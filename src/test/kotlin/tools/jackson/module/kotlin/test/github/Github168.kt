package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.MissingKotlinParameterException
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals

class TestGithub168 {
    @Suppress("UNUSED_PARAMETER")
    class TestClass(@JsonProperty(value = "foo", required = true) foo: String?, val baz: String)

    final val MAPPER: ObjectMapper = jacksonObjectMapper()

    @Test
    fun testIfRequiredIsReallyRequiredWhenNullUsed() {
        val obj = defaultMapper.readValue<TestClass>("""{"foo":null,"baz":"whatever"}""")
        assertEquals("whatever", obj.baz)
    }

    @Test
    fun testIfRequiredIsReallyRequiredWhenAbsent() {
        assertThrows<MissingKotlinParameterException> {
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
