package tools.jackson.module.kotlin.kogeraIntegration.ser.valueClass.jsonKey

import com.fasterxml.jackson.annotation.JsonKey
import tools.jackson.module.kotlin.defaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NullablePrimitiveTest {
    @JvmInline
    value class Value(val v: Int?) {
        @JsonKey
        fun jsonValue() = v?.let { it + 100 }
    }

    // The case of returning null as a key is unnecessary because it will result in an error
    @Test
    fun test() {
        assertEquals(
            """{"100":null}""",
            defaultMapper.writeValueAsString(mapOf(Value(0) to null)),
        )
    }
}
