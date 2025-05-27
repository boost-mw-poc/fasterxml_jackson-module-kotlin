package com.fasterxml.jackson.module.kotlin.test.github.failing

import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.test.expectFailure
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestGithub71 {
    open class Identifiable {
        internal var identity: Long? = null
    }

    @Test
    fun testInternalPropertySerliazation() {
        val json = defaultMapper.writeValueAsString(Identifiable())

        expectFailure<AssertionError>("GitHub #71 has been fixed!") {
            assertEquals("{\"identity\":null}", json) // fails: {"identity$jackson_module_kotlin":null}
            val newInstance = defaultMapper.readValue<Identifiable>(json)
            assertEquals(Identifiable(), newInstance)
        }
    }
}
