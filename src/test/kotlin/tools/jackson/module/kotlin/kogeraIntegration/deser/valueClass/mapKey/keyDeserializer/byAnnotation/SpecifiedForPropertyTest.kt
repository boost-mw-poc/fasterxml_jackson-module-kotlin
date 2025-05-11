package tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.mapKey.keyDeserializer.byAnnotation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue
import tools.jackson.databind.KeyDeserializer as JacksonKeyDeserializer

class SpecifiedForPropertyTest {
    @JvmInline
    value class Value(val v: Int) {
        class KeyDeserializer : JacksonKeyDeserializer() {
            override fun deserializeKey(key: String, ctxt: DeserializationContext) = Value(key.toInt() + 100)
        }
    }

    data class Wrapper(@JsonDeserialize(keyUsing = Value.KeyDeserializer::class) val v: Map<Value, String?>)

    @Test
    fun paramDeserTest() {
        val result = defaultMapper.readValue<Wrapper>("""{"v":{"1":null}}""")

        assertEquals(Wrapper(mapOf(Value(101) to null)), result)
    }
}
