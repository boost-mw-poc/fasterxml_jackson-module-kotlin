package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHub1153 {
    data class KotlinObjectWithRedactedField(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        val uri: String
    ) {
        @JsonProperty("uri", access = JsonProperty.Access.READ_ONLY)
        val redactedUri: String = uri.replace("password", "***")
    }

    @Test
    fun test() {
        val original = KotlinObjectWithRedactedField("my-password")
        val json = defaultMapper.writeValueAsString(original)
        val readValue = defaultMapper.readValue<KotlinObjectWithRedactedField>(json)
        assertEquals(KotlinObjectWithRedactedField("my-***"), readValue)
    }
}
