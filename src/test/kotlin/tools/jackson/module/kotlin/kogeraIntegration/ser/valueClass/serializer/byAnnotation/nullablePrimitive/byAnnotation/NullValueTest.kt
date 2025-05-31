package tools.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.byAnnotation.nullablePrimitive.byAnnotation

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.NullablePrimitive
import tools.jackson.module.kotlin.testPrettyWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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

        assertNotEquals(
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

        assertEquals(
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
