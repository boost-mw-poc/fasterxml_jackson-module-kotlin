package tools.jackson.module.kotlin.test.github.failing

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError
import tools.jackson.module.kotlin.defaultMapper
import kotlin.test.assertEquals

class GitHub451 {
    data class Target(
        val `foo-bar`: String,
        @get:JvmName("getBaz-qux")
        val bazQux: String
    ) {
        fun `getQuux-corge`(): String = `foo-bar`
        @JvmName("getGrault-graply")
        fun getGraultGraply(): String = bazQux
    }

    @Test
    fun serializeTest() {
        val expected = """{"foo-bar":"a","baz-qux":"b","quux-corge":"a","grault-graply":"b"}"""

        val src = Target("a", "b")
        val json = defaultMapper.writeValueAsString(src)
        assertThrows<AssertionFailedError>("GitHub #451 has been fixed!") {
            assertEquals(expected, json)
        }
    }
}
