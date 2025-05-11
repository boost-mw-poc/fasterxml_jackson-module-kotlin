package tools.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.byAnnotation.twoUnitPrimitive

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.kogeraIntegration.ser.valueClass.serializer.TwoUnitPrimitive
import tools.jackson.module.kotlin.testPrettyWriter

class ByAnnotationTest {
    companion object {
        val writer = jacksonObjectMapper().testPrettyWriter()
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

        Assertions.assertEquals(
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

        Assertions.assertEquals(
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
