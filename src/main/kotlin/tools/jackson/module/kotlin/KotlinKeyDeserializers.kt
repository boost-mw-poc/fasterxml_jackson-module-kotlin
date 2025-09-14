package tools.jackson.module.kotlin

import tools.jackson.core.JsonToken
import tools.jackson.core.exc.InputCoercionException
import tools.jackson.databind.*
import tools.jackson.databind.deser.jdk.JDKKeyDeserializer
import tools.jackson.databind.deser.jdk.JDKKeyDeserializers
import tools.jackson.databind.exc.InvalidDefinitionException
import tools.jackson.databind.util.ClassUtil
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaMethod

// The reason why key is treated as nullable is to match the tentative behavior of JDKKeyDeserializer.
// If JDKKeyDeserializer is modified, need to modify this too.

internal object UByteKeyDeserializer : JDKKeyDeserializer(TYPE_SHORT, UByte::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UByte? = super.deserializeKey(key, ctxt)
        ?.let {
            (it as Short).asUByte() ?: throw InputCoercionException(
                null,
                "Numeric value (${key}) out of range of UByte (0 - ${UByte.MAX_VALUE}).",
                JsonToken.VALUE_NUMBER_INT,
                UByte::class.java
            )
        }
}

internal object UShortKeyDeserializer : JDKKeyDeserializer(TYPE_INT, UShort::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UShort? = super.deserializeKey(key, ctxt)
        ?.let {
            (it as Int).asUShort() ?: throw InputCoercionException(
                null,
                "Numeric value (${key}) out of range of UShort (0 - ${UShort.MAX_VALUE}).",
                JsonToken.VALUE_NUMBER_INT,
                UShort::class.java
            )
        }
}

internal object UIntKeyDeserializer : JDKKeyDeserializer(TYPE_LONG, UInt::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): UInt? = super.deserializeKey(key, ctxt)
        ?.let {
            (it as Long).asUInt() ?: throw InputCoercionException(
                null,
                "Numeric value (${key}) out of range of UInt (0 - ${UInt.MAX_VALUE}).",
                JsonToken.VALUE_NUMBER_INT,
                UInt::class.java
            )
        }
}

// kind parameter is dummy.
internal object ULongKeyDeserializer : JDKKeyDeserializer(TYPE_LONG, ULong::class.java) {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext): ULong? = key?.let {
        it.toBigInteger().asULong() ?: throw InputCoercionException(
            null,
            "Numeric value (${key}) out of range of ULong (0 - ${ULong.MAX_VALUE}).",
            JsonToken.VALUE_NUMBER_INT,
            ULong::class.java
        )
    }
}

// The implementation is designed to be compatible with various creators, just in case.
internal sealed class ValueClassKeyDeserializer<S, D : Any>(
    converter: ValueClassBoxConverter<S, D>,
    creatorHandle: MethodHandle,
) : KeyDeserializer() {
    private val boxedClass: Class<D> = converter.boxedClass

    protected abstract val unboxedClass: Class<*>
    protected val handle: MethodHandle = MethodHandles.filterReturnValue(creatorHandle, converter.boxHandle)

    // Based on databind error
    // https://github.com/FasterXML/jackson-databind/blob/341f8d360a5f10b5e609d6ee0ea023bf597ce98a/src/main/java/com/fasterxml/jackson/databind/deser/DeserializerCache.java#L624
    private fun errorMessage(boxedType: JavaType): String = "Could not find (Map) Key deserializer for types " +
            "wrapped in $boxedType"

    // Since the input to handle must be strict, invoke should be implemented in each class
    protected abstract fun invokeExact(value: S): D

    final override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any {
        val unboxedJavaType = ctxt.constructType(unboxedClass)

        return try {
            // findKeyDeserializer does not return null, and an exception will be thrown if not found.
            val value = ctxt.findKeyDeserializer(unboxedJavaType, null).deserializeKey(key, ctxt)
            @Suppress("UNCHECKED_CAST")
            invokeExact(value as S)
        } catch (e: InvalidDefinitionException) {
            throw DatabindException.from(ctxt.parser, errorMessage(ctxt.constructType(boxedClass)), e)
        }
    }

    internal sealed class WrapsSpecified<S, D : Any>(
        converter: ValueClassBoxConverter<S, D>,
        creator: Method,
    ) : ValueClassKeyDeserializer<S, D>(
        converter,
        // Currently, only the primary constructor can be the creator of a key, so for specified types,
        // the return type of the primary constructor and the input type of the box function are exactly the same.
        // Therefore, performance is improved by omitting the asType call.
        unreflectWithAccessibilityModification(creator),
    )

    internal class WrapsInt<D : Any>(
        converter: IntValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<Int, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = Int::class.java

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: Int): D = handle.invokeExact(value) as D
    }

    internal class WrapsLong<D : Any>(
        converter: LongValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<Long, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = Long::class.java

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: Long): D = handle.invokeExact(value) as D
    }

    internal class WrapsString<D : Any>(
        converter: StringValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<String?, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = String::class.java

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: String?): D = handle.invokeExact(value) as D
    }

    internal class WrapsJavaUuid<D : Any>(
        converter: JavaUuidValueClassBoxConverter<D>,
        creator: Method,
    ) : WrapsSpecified<UUID?, D>(converter, creator) {
        override val unboxedClass: Class<*> get() = UUID::class.java

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: UUID?): D = handle.invokeExact(value) as D
    }

    internal class WrapsAny<S, D : Any>(
        converter: GenericValueClassBoxConverter<S, D>,
        creator: Method,
    ) : ValueClassKeyDeserializer<S, D>(
        converter,
        unreflectAsTypeWithAccessibilityModification(creator, ANY_TO_ANY_METHOD_TYPE),
    ) {
        override val unboxedClass: Class<*> = creator.returnType

        @Suppress("UNCHECKED_CAST")
        override fun invokeExact(value: S): D = handle.invokeExact(value) as D
    }

    companion object {
        fun createOrNull(
            boxedClass: KClass<*>,
            cache: ReflectionCache
        ): ValueClassKeyDeserializer<*, *>? {
            // primaryConstructor.javaMethod for the value class returns constructor-impl
            // Only primary constructor is allowed as creator, regardless of visibility.
            // This is because it is based on the WrapsNullableValueClassBoxDeserializer.
            // Also, as far as I could research, there was no such functionality as JsonKeyCreator,
            // so it was not taken into account.
            val creator = boxedClass.primaryConstructor?.javaMethod ?: return null
            val converter = cache.getValueClassBoxConverter(creator.returnType, boxedClass)

            return when (converter) {
                is IntValueClassBoxConverter -> WrapsInt(converter, creator)
                is LongValueClassBoxConverter -> WrapsLong(converter, creator)
                is StringValueClassBoxConverter -> WrapsString(converter, creator)
                is JavaUuidValueClassBoxConverter -> WrapsJavaUuid(converter, creator)
                is GenericValueClassBoxConverter -> WrapsAny(converter, creator)
            }
        }
    }
}

internal class KotlinKeyDeserializers(private val cache: ReflectionCache) : JDKKeyDeserializers() {
    override fun findKeyDeserializer(
        type: JavaType,
        config: DeserializationConfig?,
        beanDescRef: BeanDescription.Supplier?,
    ): KeyDeserializer? {
        val rawClass = type.rawClass

        return when {
            rawClass == UByte::class.java -> UByteKeyDeserializer
            rawClass == UShort::class.java -> UShortKeyDeserializer
            rawClass == UInt::class.java -> UIntKeyDeserializer
            rawClass == ULong::class.java -> ULongKeyDeserializer
            rawClass.isUnboxableValueClass() -> ValueClassKeyDeserializer.createOrNull(rawClass.kotlin, cache)
            else -> null
        }
    }
}
