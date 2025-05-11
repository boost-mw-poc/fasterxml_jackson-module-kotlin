package tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.deserializer.byAnnotation.specifiedForProperty

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NullableObject
import tools.jackson.module.kotlin.readValue

class NullableObjectTest {
    data class NonNull(
        @get:JsonDeserialize(using = NullableObject.DeserializerWrapsNullable::class)
        val getterAnn: NullableObject,
        @field:JsonDeserialize(using = NullableObject.DeserializerWrapsNullable::class)
        val fieldAnn: NullableObject
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
        assertEquals(NonNull(NullableObject("foo-deser"), NullableObject("bar-deser")), result)
    }

    data class Nullable(
        @get:JsonDeserialize(using = NullableObject.DeserializerWrapsNullable::class)
        val getterAnn: NullableObject?,
        @field:JsonDeserialize(using = NullableObject.DeserializerWrapsNullable::class)
        val fieldAnn: NullableObject?
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
            assertEquals(Nullable(NullableObject("foo-deser"), NullableObject("bar-deser")), result)
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
