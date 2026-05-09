package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals

class GitHub341 {
    data class TestMe1(
        val property1: String,
        val property2: String
    ) {
        private fun getProperty1(): Int = 3
    }

    @Test
    fun defaultTest() {
        val json = defaultMapper.writeValueAsString(TestMe1(property1 = "prop1", property2 = "prop2"))
        val deserialized = defaultMapper.readValue<Map<String, String>>(json)
        val expected = mapOf("property1" to "prop1", "property2" to "prop2")
        assertEquals(expected, deserialized)
    }

    @Test
    fun kotlinPropertyNameAsImplicitNameTest() {
        val mapper = jacksonObjectMapper { enable(KotlinFeature.KotlinPropertyNameAsImplicitName) }
        val json = mapper.writeValueAsString(TestMe1(property1 = "prop1", property2 = "prop2"))
        val deserialized = mapper.readValue<Map<String, String>>(json)
        val expected = mapOf("property1" to "prop1", "property2" to "prop2")
        assertEquals(expected, deserialized)
    }
}
