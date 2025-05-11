package tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.deserializer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tools.jackson.databind.module.SimpleModule
import tools.jackson.module.kotlin.jacksonMapperBuilder
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NonNullObject
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NullableObject
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.Primitive
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.TwoUnitPrimitive
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertNotEquals

class SpecifiedForObjectMapperTest {
    companion object {
        val mapper = jacksonMapperBuilder().apply {
            val module = SimpleModule().apply {
                this.addDeserializer(Primitive::class.java, Primitive.Deserializer())
                this.addDeserializer(NonNullObject::class.java, NonNullObject.Deserializer())
                this.addDeserializer(NullableObject::class.java, NullableObject.DeserializerWrapsNullable())
                this.addDeserializer(TwoUnitPrimitive::class.java, TwoUnitPrimitive.Deserializer())
            }
            this.addModule(module)
        }.build()
    }

    @Nested
    inner class DirectDeserialize {
        @Test
        fun primitive() {
            val result = mapper.readValue<Primitive>("1")
            assertEquals(Primitive(101), result)
        }

        @Test
        fun nonNullObject() {
            val result = mapper.readValue<NonNullObject>(""""foo"""")
            assertEquals(NonNullObject("foo-deser"), result)
        }

        @Suppress("ClassName")
        @Nested
        inner class NullableObject_ {
            @Test
            fun value() {
                val result = mapper.readValue<NullableObject>(""""foo"""")
                assertEquals(NullableObject("foo-deser"), result)
            }

            // failing
            @Test
            fun nullString() {
                val result = mapper.readValue<NullableObject?>("null")
                assertNotEquals(NullableObject("null-value-deser"), result, "#209 has been fixed.")
            }
        }

        @Test
        fun twoUnitPrimitive() {
            val result = mapper.readValue<TwoUnitPrimitive>("1")
            assertEquals(TwoUnitPrimitive(101), result)
        }
    }

    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?,
        val tupNn: TwoUnitPrimitive,
        val tupN: TwoUnitPrimitive?
    )

    @Test
    fun nonNull() {
        val base = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("foo"),
            NonNullObject("bar"),
            NullableObject("baz"),
            NullableObject("qux"),
            TwoUnitPrimitive(3),
            TwoUnitPrimitive(4)
        )
        val src = mapper.writeValueAsString(base)
        val result = mapper.readValue<Dst>(src)

        val expected = Dst(
            Primitive(101),
            Primitive(102),
            NonNullObject("foo-deser"),
            NonNullObject("bar-deser"),
            NullableObject("baz-deser"),
            NullableObject("qux-deser"),
            TwoUnitPrimitive(103),
            TwoUnitPrimitive(104)
        )
        assertEquals(expected, result)
    }

    @Test
    fun withNull() {
        val base = Dst(
            Primitive(1),
            null,
            NonNullObject("foo"),
            null,
            NullableObject(null),
            null,
            TwoUnitPrimitive(3),
            null
        )
        val src = mapper.writeValueAsString(base)
        val result = mapper.readValue<Dst>(src)

        val expected = Dst(
            Primitive(101),
            null,
            NonNullObject("foo-deser"),
            null,
            NullableObject("null-value-deser"),
            null,
            TwoUnitPrimitive(103),
            null
        )
        assertEquals(expected, result)
    }
}
