package tools.jackson.module.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tools.jackson.databind.deser.std.StdValueInstantiator

class KotlinInstantiatorsTest {
    private val deserConfig = defaultMapper.deserializationConfig()

    private val kotlinInstantiators = KotlinInstantiators(
        ReflectionCache(10),
        nullToEmptyCollection = false,
        nullToEmptyMap = false,
        nullIsSameAsDefault = false,
        strictNullChecks = false
    )

    @Test
    fun `Provides default instantiator for Java class`() {
        val javaType = defaultMapper.constructType(String::class.java)
        val defaultInstantiator = StdValueInstantiator(deserConfig, javaType)
        val classIntrospector = deserConfig.classIntrospectorInstance()
        val instantiator = kotlinInstantiators.modifyValueInstantiator(
            deserConfig,
            classIntrospector.introspectForDeserialization(javaType,
                classIntrospector.introspectClassAnnotations(javaType)).supplier(),
            defaultInstantiator
        )

        assertEquals(defaultInstantiator, instantiator)
    }

    @Test
    fun `Provides KotlinValueInstantiator for Kotlin class`() {
        class TestClass

        val javaType = defaultMapper.constructType(TestClass::class.java)
        val classIntrospector = deserConfig.classIntrospectorInstance()
        val instantiator = kotlinInstantiators.modifyValueInstantiator(
            deserConfig,
            classIntrospector.introspectForDeserialization(javaType,
                classIntrospector.introspectClassAnnotations(javaType)).supplier(),
            StdValueInstantiator(deserConfig, javaType)
        )

        assertTrue(instantiator is StdValueInstantiator)
        assertTrue(instantiator::class == KotlinValueInstantiator::class)
    }

    @Test
    fun `Throws for Kotlin class when default instantiator isn't StdValueInstantiator`() {
        class TestClass
        class DefaultClass

        val subClassInstantiator = object : StdValueInstantiator(
            deserConfig,
            defaultMapper.constructType(DefaultClass::class.java)
        ) {}

        assertThrows(IllegalStateException::class.java) {
            val javaType = defaultMapper.constructType(TestClass::class.java)
            val classIntrospector = deserConfig.classIntrospectorInstance()
            kotlinInstantiators.modifyValueInstantiator(
                deserConfig,
                classIntrospector.introspectForDeserialization(javaType,
                    classIntrospector.introspectClassAnnotations(javaType)).supplier(),
                subClassInstantiator
            )
        }
    }
}
