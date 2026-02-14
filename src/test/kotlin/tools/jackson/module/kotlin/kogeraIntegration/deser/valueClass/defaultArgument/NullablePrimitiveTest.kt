package tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.defaultArgument

import com.fasterxml.jackson.annotation.JsonCreator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NullablePrimitive
import tools.jackson.module.kotlin.readValue

class NullablePrimitiveTest {
    data class ByConstructor(
        val nnNn: NullablePrimitive = NullablePrimitive(1),
        val nnN: NullablePrimitive = NullablePrimitive(null),
        val nNn: NullablePrimitive? = NullablePrimitive(2),
        val nN: NullablePrimitive? = null
    )

    @Test
    fun byConstructorTestFailing() {
        // #761(KT-57357) fixed
        if (KotlinVersion.CURRENT >= KotlinVersion(2, 3, 20)) {
            assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
        } else {
            Assertions.assertThrows(Error::class.java) {
                assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
            }
        }
    }

    data class ByFactory(
        val nnNn: NullablePrimitive = NullablePrimitive(1),
        val nnN: NullablePrimitive = NullablePrimitive(null),
        val nNn: NullablePrimitive? = NullablePrimitive(2),
        val nN: NullablePrimitive? = null
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nnNn: NullablePrimitive = NullablePrimitive(1),
                nnN: NullablePrimitive = NullablePrimitive(null),
                nNn: NullablePrimitive? = NullablePrimitive(2),
                nN: NullablePrimitive? = null
            ) = ByFactory(nnNn, nnN, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        // #761(KT-57357) fixed
        if (KotlinVersion.CURRENT >= KotlinVersion(2, 3, 20)) {
            assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
        } else {
            Assertions.assertThrows(Error::class.java) {
                assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
            }
        }
    }
}
