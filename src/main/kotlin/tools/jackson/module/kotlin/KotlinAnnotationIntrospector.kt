package tools.jackson.module.kotlin

import tools.jackson.databind.cfg.MapperConfig
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.introspect.AnnotatedClass
import tools.jackson.databind.introspect.AnnotatedMethod
import tools.jackson.databind.introspect.NopAnnotationIntrospector
import tools.jackson.databind.jsontype.NamedType
import tools.jackson.databind.util.Converter
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class KotlinAnnotationIntrospector(
    private val cache: ReflectionCache,
    private val useJavaDurationConversion: Boolean,
    private val useJavaInstantConversion: Boolean,
) : NopAnnotationIntrospector() {

    override fun findSerializationConverter(config: MapperConfig<*>?, a: Annotated): Converter<*, *>? = when (a) {
        // Find a converter to handle the case where the getter returns an unboxed value from the value class.
        is AnnotatedMethod -> a.findValueClassReturnType()?.let {
            // To make annotations that process JavaDuration work,
            // it is necessary to set up the conversion to JavaDuration here.
            // This conversion will cause the deserialization settings for KotlinDuration to be ignored.
            if (useJavaDurationConversion && it == Duration::class) {
                // For early return, the same process is placed as the branch regarding AnnotatedClass.
                if (a.rawReturnType == Duration::class.java)
                    KotlinToJavaDurationConverter
                else
                    KotlinDurationValueToJavaDurationConverter
            } else {
                cache.getValueClassBoxConverter(a.rawReturnType, it)
            }
        }
        is AnnotatedClass -> lookupKotlinTypeConverter(a)
        else -> null
    }

    @OptIn(ExperimentalTime::class)
    private fun lookupKotlinTypeConverter(a: AnnotatedClass) = when {
        Sequence::class.java.isAssignableFrom(a.rawType) -> SequenceToIteratorConverter(a.type)
        Duration::class.java == a.rawType -> KotlinToJavaDurationConverter.takeIf { useJavaDurationConversion }
        Instant::class.java == a.rawType -> KotlinToJavaInstantConverter.takeIf { useJavaInstantConversion }
        else -> null
    }

    // Perform proper serialization even if the value wrapped by the value class is null.
    // If value is a non-null object type, it must not be reboxing.
    override fun findNullSerializer(config: MapperConfig<*>?, am: Annotated) = (am as? AnnotatedMethod)
        ?.findValueClassReturnType()
        ?.takeIf { it.wrapsNullable() }
        ?.let { cache.getValueClassBoxConverter(am.rawReturnType, it).delegatingSerializer }

    /**
     * Subclasses can be detected automatically for sealed classes, since all possible subclasses are known
     * at compile-time to Kotlin. This makes [com.fasterxml.jackson.annotation.JsonSubTypes] redundant.
     */
    override fun findSubtypes(cfg: MapperConfig<*>, a: Annotated): MutableList<NamedType>? = a.rawType
        .takeIf { it.isKotlinClass() }
        ?.let { rawType ->
            rawType.kotlin.sealedSubclasses
                .map { NamedType(it.java) }
                .toMutableList()
                .ifEmpty { null }
        }

    private fun AnnotatedMethod.findValueClassReturnType() = cache.findValueClassReturnType(this)
}
