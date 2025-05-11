package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals

class GitHub832 {
    data class AnySetter @JvmOverloads constructor(
        val test: String? = null,
        @JsonAnySetter
        @get:JsonAnyGetter
        val anything: Map<String, Any?> = mutableMapOf(),
    )

    @Test
    fun testDeserialization() {
        val json = """
            {
                "widget": {
                    "debug": "on"
                 }
             }     """.trimMargin()
        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()
        val anySetter = mapper.readValue<AnySetter>(json)
        assertEquals("widget", anySetter.anything.entries.first().key)
    }
}
