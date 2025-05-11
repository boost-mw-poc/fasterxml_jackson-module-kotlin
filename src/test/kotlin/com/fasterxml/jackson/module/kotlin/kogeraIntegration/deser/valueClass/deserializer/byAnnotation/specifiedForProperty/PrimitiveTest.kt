package com.fasterxml.jackson.module.kotlin.kogeraIntegration.deser.valueClass.deserializer.byAnnotation.specifiedForProperty

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.kogeraIntegration.deser.valueClass.Primitive
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PrimitiveTest {
    data class NonNull(
        @get:JsonDeserialize(using = Primitive.Deserializer::class)
        val getterAnn: Primitive,
        @field:JsonDeserialize(using = Primitive.Deserializer::class)
        val fieldAnn: Primitive
    )

    @Test
    fun nonNull() {
        val result = defaultMapper.readValue<NonNull>(
            """
                {
                  "getterAnn" : 1,
                  "fieldAnn" : 2
                }
            """.trimIndent()
        )
        assertEquals(NonNull(Primitive(101), Primitive(102)), result)
    }

    data class Nullable(
        @get:JsonDeserialize(using = Primitive.Deserializer::class)
        val getterAnn: Primitive?,
        @field:JsonDeserialize(using = Primitive.Deserializer::class)
        val fieldAnn: Primitive?
    )

    @Nested
    inner class NullableTest {
        @Test
        fun nonNullInput() {
            val result = defaultMapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : 1,
                  "fieldAnn" : 2
                }
                """.trimIndent()
            )
            assertEquals(Nullable(Primitive(101), Primitive(102)), result)
        }

        @Test
        fun nullInput() {
            val result = defaultMapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
                """.trimIndent()
            )
            assertEquals(Nullable(null, null), result)
        }
    }
}
