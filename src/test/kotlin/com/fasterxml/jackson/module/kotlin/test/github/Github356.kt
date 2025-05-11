package com.fasterxml.jackson.module.kotlin.test.github

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestGithub356 {
    @Test
    fun deserializeInlineClass() {
        assertEquals(
            ClassWithInlineMember(InlineClass("bar")),
            defaultMapper.readValue("""{"inlineClassProperty":"bar"}""")
        )
    }

    @Test
    fun serializeInlineClass() {
        assertEquals(
            """{"inlineClassProperty":"bar"}""",
            defaultMapper.writeValueAsString(ClassWithInlineMember(InlineClass("bar")))
        )
    }

    @Test
    fun deserializeValueClass() {
        assertEquals(
            ClassWithValueMember(ValueClass("bar")),
            defaultMapper.readValue("""{"valueClassProperty":"bar"}""")
        )
    }

    @Test
    fun serializeValueClass() {
        assertEquals(
            """{"valueClassProperty":"bar"}""",
            defaultMapper.writeValueAsString(ClassWithValueMember(ValueClass("bar")))
        )
    }
}

@JvmInline
value class InlineClass(val value: String)

@JsonDeserialize(builder = ClassWithInlineMember.JacksonBuilder::class)
data class ClassWithInlineMember(val inlineClassProperty: InlineClass) {
    data class JacksonBuilder constructor(val inlineClassProperty: String) {
        fun build() = ClassWithInlineMember(InlineClass(inlineClassProperty))
    }
}

@JvmInline
value class ValueClass(val value: String)

@JsonDeserialize(builder = ClassWithValueMember.JacksonBuilder::class)
data class ClassWithValueMember(val valueClassProperty: ValueClass) {
    data class JacksonBuilder constructor(val valueClassProperty: String) {
        fun build() = ClassWithValueMember(ValueClass(valueClassProperty))
    }
}
