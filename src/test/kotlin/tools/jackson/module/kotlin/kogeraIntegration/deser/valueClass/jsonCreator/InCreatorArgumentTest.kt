package tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.jsonCreator

import com.fasterxml.jackson.annotation.JsonCreator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NonNullObject
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NullableObject
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.Primitive
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.TwoUnitPrimitive
import tools.jackson.module.kotlin.readValue

private fun Primitive.modify(): Primitive = Primitive(v + 100)
private fun NonNullObject.modify(): NonNullObject = NonNullObject("$v-creator")
private fun NullableObject.modify(): NullableObject = NullableObject(v!! + "-creator")
private fun TwoUnitPrimitive.modify(): TwoUnitPrimitive = TwoUnitPrimitive(v + 100)

class InCreatorArgumentTest {
    data class Dst(
        val pNn: Primitive,
        val pN: Primitive?,
        val nnoNn: NonNullObject,
        val nnoN: NonNullObject?,
        val noNn: NullableObject,
        val noN: NullableObject?,
        val tupNn: TwoUnitPrimitive,
        val tupN: TwoUnitPrimitive?
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                pNn: Primitive,
                pN: Primitive?,
                nnoNn: NonNullObject,
                nnoN: NonNullObject?,
                noNn: NullableObject,
                noN: NullableObject?,
                tupNn: TwoUnitPrimitive,
                tupN: TwoUnitPrimitive?
            ) = Dst(
                pNn.modify(),
                pN?.modify(),
                nnoNn.modify(),
                nnoN?.modify(),
                noNn.modify(),
                noN?.modify(),
                tupNn.modify(),
                tupN?.modify()
            )
        }
    }

    @Test
    fun test() {
        val base = Dst(
            Primitive(1),
            Primitive(2),
            NonNullObject("nnoNn"),
            NonNullObject("nnoN"),
            NullableObject("noNn"),
            NullableObject("noN"),
            TwoUnitPrimitive(3),
            TwoUnitPrimitive(4)
        )
        val result = defaultMapper.readValue<Dst>(defaultMapper.writeValueAsString(base))

        assertEquals(
            base.copy(
                pNn = base.pNn.modify(),
                pN = base.pN?.modify(),
                nnoNn = base.nnoNn.modify(),
                nnoN = base.nnoN?.modify(),
                noNn = base.noNn.modify(),
                noN = base.noN?.modify(),
                tupNn = base.tupNn.modify(),
                tupN = base.tupN?.modify()
            ),
            result
        )
    }
}
