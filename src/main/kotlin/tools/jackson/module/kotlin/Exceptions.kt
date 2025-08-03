package tools.jackson.module.kotlin

import tools.jackson.core.JsonParser
import tools.jackson.databind.DatabindException
import tools.jackson.databind.exc.InvalidNullException
import kotlin.reflect.KParameter

/**
 * Specialized [DatabindException] sub-class used to indicate that a mandatory Kotlin constructor
 * parameter was missing or null.
 */
@Deprecated(
    "Since 2.20, this exception is no longer thrown and has been replaced by KotlinInvalidNullException. " +
            "See #617 for details.",
    ReplaceWith("KotlinInvalidNullException"),
    DeprecationLevel.ERROR
)
// When deserialized by the JDK, the parameter property will be null, ignoring nullability.
// This is a temporary workaround for #572 and we will eventually remove this class.
class MissingKotlinParameterException(
    @property:Deprecated(
        "KParameter is not serializable and will be removed in 2.20 or later. See #572 for details.",
        level = DeprecationLevel.WARNING
    )
    @Transient
    val parameter: KParameter,
    processor: JsonParser? = null,
    msg: String
) : InvalidNullException(processor, msg, null)
