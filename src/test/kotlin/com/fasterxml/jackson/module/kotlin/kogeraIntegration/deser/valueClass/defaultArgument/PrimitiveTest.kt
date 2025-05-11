package com.fasterxml.jackson.module.kotlin.kogeraIntegration.deser.valueClass.defaultArgument

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.kotlin.defaultMapper
import com.fasterxml.jackson.module.kotlin.kogeraIntegration.deser.valueClass.Primitive
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrimitiveTest {
    data class ByConstructor(
        val nn: Primitive = Primitive(1),
        val nNn: Primitive? = Primitive(2),
        val nN: Primitive? = null
    )

    @Test
    fun byConstructorTest() {
        assertEquals(ByConstructor(), defaultMapper.readValue<ByConstructor>("{}"))
    }

    data class ByFactory(val nn: Primitive, val nNn: Primitive?, val nN: Primitive?) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun creator(
                nn: Primitive = Primitive(1),
                nNn: Primitive? = Primitive(2),
                nN: Primitive? = null
            ) = ByFactory(nn, nNn, nN)
        }
    }

    @Test
    fun byFactoryTest() {
        assertEquals(ByFactory.creator(), defaultMapper.readValue<ByFactory>("{}"))
    }
}
