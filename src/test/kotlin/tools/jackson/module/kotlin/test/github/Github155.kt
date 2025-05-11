package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue

class TestGithub155 {
    data class Foo @JvmOverloads constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("age") val age: Int = 0,
        @JsonProperty("country") val country: String = "whatever",
        @JsonProperty("city") val city: String = "nada")

    @Test
    fun testGithub155() {
        defaultMapper.readValue<Foo>("""
            {"name":"fred","age":12,"country":"Libertad","city":"Northville"}
        """.trimIndent())
    }
}
