package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue
import java.util.Locale

class GitHub1050Test {
    @JvmInline
    value class PortCode private constructor(val value: String) : Comparable<PortCode> {
        override fun compareTo(other: PortCode): Int { TODO("Not yet implemented") }

        companion object {
            operator fun invoke(value: String) = PortCode(value.uppercase(Locale.getDefault()))
        }
    }

    @Test
    fun test() {
        val result = defaultMapper.readValue<PortCode>("\"ABC\"")
        assertEquals("ABC", result.value)
    }
}
