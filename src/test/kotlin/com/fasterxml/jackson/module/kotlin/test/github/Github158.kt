package com.fasterxml.jackson.module.kotlin.test.github

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestGithub158 {
    enum class SampleImpl constructor(override val value: String): Sample {
        One("oney"),
        Two("twoey")
    }

    interface Sample {
        val value: String
    }

    data class SampleContainer(@JsonDeserialize(`as` = SampleImpl::class) val sample: Sample)

    @Test
    fun testEnumSerDeser() {
        val original = SampleContainer(SampleImpl.One)

        val json = defaultMapper.writeValueAsString(original)
//        println(json)
        val obj = defaultMapper.readValue<SampleContainer>(json)
        assertEquals(original, obj)
    }
}
