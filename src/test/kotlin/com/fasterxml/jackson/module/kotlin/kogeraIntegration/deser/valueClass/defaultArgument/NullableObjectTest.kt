package com.fasterxml.jackson.module.kotlin.kogeraIntegration.deser.valueClass.defaultArgument

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.kogeraIntegration.deser.valueClass.NullableObject
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NullableObjectTest {
    data class ByConstructor(
        val nnNn: NullableObject = NullableObject("foo"),
        val nnN: NullableObject = NullableObject(null),
        val nNn: NullableObject? = NullableObject("bar"),
        val nN: NullableObject? = null
    )

    @Test
    fun byConstructorTestFailing() {
        // #761(KT-57357) fixed
        if (KotlinVersion.CURRENT >= KotlinVersion(2, 3, 20)) {
            assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
        } else {
            assertThrows(Error::class.java) {
                assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
            }
        }
    }

    data class ByFactory(
        val nnNn: NullableObject = NullableObject("foo"),
        val nnN: NullableObject = NullableObject(null),
        val nNn: NullableObject? = NullableObject("bar"),
        val nN: NullableObject? = null
    ) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nn: NullableObject = NullableObject("foo"),
                nnN: NullableObject = NullableObject(null),
                nNn: NullableObject? = NullableObject("bar"),
                nN: NullableObject? = null
            ) = ByFactory(nn, nnN, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        // #761(KT-57357) fixed
        if (KotlinVersion.CURRENT >= KotlinVersion(2, 3, 20)) {
            assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
        } else {
            assertThrows(Error::class.java) {
                assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
            }
        }
    }
}
