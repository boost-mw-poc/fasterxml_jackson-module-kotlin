package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue

//TODO : Fix @JsonIgnore
class TestGithub124 {
    class NonSerializable(private val field: Any?) {
        override fun toString() = "NonSerializable"
    }

    data class Foo @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("query") val rawQuery: String)


    @Test
    fun test() {
        val deserialized: Foo = defaultMapper.readValue("""{"name": "foo", "query": "bar"}""")
        val serialized = defaultMapper.writeValueAsString(deserialized)

        assert(serialized == """{"name":"foo","query":"bar"}""")
    }
}
