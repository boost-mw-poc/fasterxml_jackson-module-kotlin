package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import kotlin.test.assertEquals

class TestGithub52 {
    @Test
    fun testBooleanPropertyInConstructor() {
        data class BooleanPropertyInConstructor(
                @JsonProperty("is_bar")
                val bar: Boolean = true
        )

        assertEquals("""{"is_bar":true}""", defaultMapper.writeValueAsString(BooleanPropertyInConstructor()))
    }

    @Test
    fun testIsPrefixedBooleanPropertyInConstructor() {
        data class IsPrefixedBooleanPropertyInConstructor(
                @JsonProperty("is_bar2")
                val isBar2: Boolean = true
        )

        assertEquals("""{"is_bar2":true}""", defaultMapper.writeValueAsString(IsPrefixedBooleanPropertyInConstructor()))
    }

    @Test
    fun testIsPrefixedStringPropertyInConstructor() {
        data class IsPrefixedStringPropertyInConstructor(
                @JsonProperty("is_lol")
                val lol: String = "sdf"
        )

        assertEquals("""{"is_lol":"sdf"}""", defaultMapper.writeValueAsString(IsPrefixedStringPropertyInConstructor()))
    }

    @Test
    fun testBooleanPropertyInBody() {
        data class BooleanPropertyInBody(
                @JsonIgnore val placeholder: String = "placeholder"
        ) {
            @JsonProperty("is_foo")
            val foo: Boolean = true
        }

        assertEquals("""{"is_foo":true}""", defaultMapper.writeValueAsString(BooleanPropertyInBody()))
    }

    @Test
    fun testIsPrefixedBooleanPropertyInBody() {
        data class IsPrefixedBooleanPropertyInBody(
                @JsonIgnore val placeholder: String = "placeholder"
        ) {
            @JsonProperty("is_foo2")
            val isFoo2: Boolean = true
        }

        assertEquals("""{"is_foo2":true}""", defaultMapper.writeValueAsString(IsPrefixedBooleanPropertyInBody()))
    }
}
