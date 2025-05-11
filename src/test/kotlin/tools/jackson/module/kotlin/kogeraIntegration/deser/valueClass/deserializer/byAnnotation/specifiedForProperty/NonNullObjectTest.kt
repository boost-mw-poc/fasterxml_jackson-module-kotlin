package tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.deserializer.byAnnotation.specifiedForProperty

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NonNullObject
import tools.jackson.module.kotlin.readValue

class NonNullObjectTest {
    data class NonNull(
        @get:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val getterAnn: NonNullObject,
        @field:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val fieldAnn: NonNullObject
    )

    @Test
    fun nonNull() {
        val result = defaultMapper.readValue<NonNull>(
            """
                {
                  "getterAnn" : "foo",
                  "fieldAnn" : "bar"
                }
            """.trimIndent()
        )
        assertEquals(NonNull(NonNullObject("foo-deser"), NonNullObject("bar-deser")), result)
    }

    data class Nullable(
        @get:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val getterAnn: NonNullObject?,
        @field:JsonDeserialize(using = NonNullObject.Deserializer::class)
        val fieldAnn: NonNullObject?
    )

    @Nested
    inner class NullableTest {
        @Test
        fun nonNullInput() {
            val result = defaultMapper.readValue<Nullable>(
                """
                {
                  "getterAnn" : "foo",
                  "fieldAnn" : "bar"
                }
                """.trimIndent()
            )
            assertEquals(Nullable(NonNullObject("foo-deser"), NonNullObject("bar-deser")), result)
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
