package com.fasterxml.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.byAnnotation.nullablePrimitive

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.NullablePrimitive
import com.fasterxml.jackson.module.kotlin.testPrettyWriter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NullValueTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
    }

    data class NonNullSrc(
        @get:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val getterAnn: NullablePrimitive,
        @field:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val fieldAnn: NullablePrimitive
    )

    @Test
    fun failing() {
        val src = NonNullSrc(NullablePrimitive(null), NullablePrimitive(null))

        Assertions.assertNotEquals(
            """
                {
                  "getterAnn" : "NULL",
                  "fieldAnn" : "NULL"
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    data class NullableSrc(
        @get:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val getterAnn: NullablePrimitive?,
        @field:JsonSerialize(using = NullablePrimitive.Serializer::class)
        val fieldAnn: NullablePrimitive?
    )

    @Test
    fun nullableWithoutNull() {
        val src = NullableSrc(NullablePrimitive(null), NullablePrimitive(null))

        Assertions.assertEquals(
            """
                {
                  "getterAnn" : "NULL",
                  "fieldAnn" : "NULL"
                }
            """.trimIndent(),
            writer.writeValueAsString(src)
        )
    }

    @Test
    fun nullableWithNull() {
        val src = NullableSrc(null, null)

        Assertions.assertEquals(
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
