package com.fasterxml.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.byAnnotation.twoUnitPrimitive

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.TwoUnitPrimitive
import com.fasterxml.jackson.module.kotlin.testPrettyWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByAnnotationTest {
    companion object {
        val writer = KotlinModule.Builder()
            .build()
            .let { ObjectMapper().registerModule(it) }
            .testPrettyWriter()
    }

    data class NonNullSrc(
        // @see #651
        // @JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        // val paramAnn: TwoUnitPrimitive,
        @get:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val getterAnn: TwoUnitPrimitive,
        @field:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val fieldAnn: TwoUnitPrimitive
    )

    @Test
    fun nonNull() {
        val src = NonNullSrc(/* TwoUnitPrimitive(0),*/ TwoUnitPrimitive(1), TwoUnitPrimitive(2))

        assertEquals(
            """
                {
                  "getterAnn" : 101,
                  "fieldAnn" : 102
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class NullableSrc(
        // @see #651
        // @JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        // val paramAnn: TwoUnitPrimitive?,
        @get:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val getterAnn: TwoUnitPrimitive?,
        @field:JsonSerialize(using = TwoUnitPrimitive.Serializer::class)
        val fieldAnn: TwoUnitPrimitive?
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(/* TwoUnitPrimitive(0),*/ TwoUnitPrimitive(1), TwoUnitPrimitive(2))

        assertEquals(
            """
                {
                  "getterAnn" : 101,
                  "fieldAnn" : 102
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    @Test
    fun nullableWithNull() {
        val src = NullableSrc(null, null)

        assertEquals(
            """
                {
                  "getterAnn" : null,
                  "fieldAnn" : null
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }
}
