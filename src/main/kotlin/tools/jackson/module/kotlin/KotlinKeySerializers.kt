package tools.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonKey
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.JavaType
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.ser.Serializers
import tools.jackson.databind.ser.std.StdSerializer
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal class ValueClassUnboxKeySerializer<T : Any>(
    private val converter: ValueClassUnboxConverter<T, *>,
) : StdSerializer<T>(converter.valueClass) {
    override fun serialize(value: T, gen: JsonGenerator, ctxt: SerializationContext) {
        val unboxed = converter.convert(value)

        if (unboxed == null) {
            val javaType = converter.getOutputType(ctxt.typeFactory)
            ctxt.findNullKeySerializer(javaType, null).serialize(null, gen, ctxt)
            return
        }

        ctxt.findKeySerializer(unboxed::class.java, null).serialize(unboxed, gen, ctxt)
    }
}

internal sealed class ValueClassStaticJsonKeySerializer<T : Any>(
    converter: ValueClassUnboxConverter<T, *>,
    staticJsonValueGetter: Method,
    methodType: MethodType,
) : StdSerializer<T>(converter.valueClass) {
    private val keyType: Class<*> = staticJsonValueGetter.returnType
    private val handle: MethodHandle = unreflectAsTypeWithAccessibilityModification(staticJsonValueGetter, methodType).let {
        MethodHandles.filterReturnValue(converter.unboxHandle, it)
    }

    final override fun serialize(value: T, gen: JsonGenerator, ctxt: SerializationContext) {
        val jsonKey: Any? = handle.invokeExact(value)

        val serializer = jsonKey
            ?.let { ctxt.findKeySerializer(keyType, null) }
            ?: ctxt.findNullKeySerializer(ctxt.constructType(keyType), null)

        serializer.serialize(jsonKey, gen, ctxt)
    }

    internal class WrapsInt<T : Any>(
        converter: IntValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        INT_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsLong<T : Any>(
        converter: LongValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        LONG_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsString<T : Any>(
        converter: StringValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        STRING_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsJavaUuid<T : Any>(
        converter: JavaUuidValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        JAVA_UUID_TO_ANY_METHOD_TYPE,
    )

    internal class WrapsAny<T : Any>(
        converter: GenericValueClassUnboxConverter<T>,
        staticJsonValueGetter: Method,
    ) : ValueClassStaticJsonKeySerializer<T>(
        converter,
        staticJsonValueGetter,
        ANY_TO_ANY_METHOD_TYPE,
    )

    companion object {
        // Class must be UnboxableValueClass.
        private fun Class<*>.getStaticJsonKeyGetter(): Method? = this.declaredMethods.find { method ->
            Modifier.isStatic(method.modifiers) && method.annotations.any { it is JsonKey && it.value }
        }

        // `t` must be UnboxableValueClass.
        // If create a function with a JsonValue in the value class,
        // it will be compiled as a static method (= cannot be processed properly by Jackson),
        // so use a ValueClassSerializer.StaticJsonValue to handle this.
        fun <T : Any> createOrNull(
            converter: ValueClassUnboxConverter<T, *>,
        ): ValueClassStaticJsonKeySerializer<T>? = converter
            .valueClass
            .getStaticJsonKeyGetter()
            ?.let {
                when (converter) {
                    is IntValueClassUnboxConverter -> WrapsInt(converter, it)
                    is LongValueClassUnboxConverter -> WrapsLong(converter, it)
                    is StringValueClassUnboxConverter -> WrapsString(converter, it)
                    is JavaUuidValueClassUnboxConverter -> WrapsJavaUuid(converter, it)
                    is GenericValueClassUnboxConverter -> WrapsAny(converter, it)
                }
            }
    }
}

internal class KotlinKeySerializers(private val cache: ReflectionCache) : Serializers.Base() {
    override fun findSerializer(
        config: SerializationConfig,
        type: JavaType,
        beanDescRef: BeanDescription.Supplier,
        formatOverrides: JsonFormat.Value?
    ): ValueSerializer<*>? {
        val rawClass = type.rawClass

        return when {
            rawClass.isUnboxableValueClass() -> {
                val unboxConverter = cache.getValueClassUnboxConverter(rawClass)
                ValueClassStaticJsonKeySerializer.createOrNull(unboxConverter)
                    ?: ValueClassUnboxKeySerializer(unboxConverter)
            }
            else -> null
        }
    }
}
