package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertSame

/**
 * An empty object should be deserialized as *the* Unit instance
 */
class TestGithub196 {
    @Test
    fun testUnitSingletonDeserialization() {
        assertSame(defaultMapper.readValue("{}"), Unit)
    }
}
