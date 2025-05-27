package com.fasterxml.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestGithub22 {
    class StringValue constructor(s: String) {
        val other: String = s

        @JsonValue override fun toString() = other
    }

    data class StringValue2(@get:JsonIgnore val s: String) {
        @JsonValue override fun toString() = s
    }

    @Test fun testJsonValueNoMatchingMemberWithConstructor() {
        val expectedJson = "\"test\""
        val expectedObj = StringValue("test")

        val actualJson = defaultMapper.writeValueAsString(expectedObj)
        assertEquals(expectedJson, actualJson)

        val actualObj = defaultMapper.readValue<StringValue>("\"test\"")
        assertEquals(expectedObj.other, actualObj.other)

    }

    @Test fun testJsonValue2DataClassIgnoredMemberInConstructor() {
        val expectedJson = "\"test\""
        val expectedObj = StringValue2("test")

        val actualJson = defaultMapper.writeValueAsString(expectedObj)
        assertEquals(expectedJson, actualJson)

        val actualObj = defaultMapper.readValue<StringValue2>("\"test\"")
        assertEquals(expectedObj, actualObj)

    }
}