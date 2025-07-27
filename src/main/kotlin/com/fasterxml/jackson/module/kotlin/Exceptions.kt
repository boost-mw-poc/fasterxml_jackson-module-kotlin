package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidNullException
import kotlin.reflect.KParameter

/**
 * Specialized [JsonMappingException] sub-class used to indicate that a mandatory Kotlin constructor
 * parameter was missing or null.
 */
@Deprecated(
    "It is recommended that InvalidNullException be referenced when possible," +
            " as the change is discussed for 2.20 and later." +
            " See #617 for details.",
    ReplaceWith(
        "InvalidNullException",
        "com.fasterxml.jackson.databind.exc.InvalidNullException"
    ),
    DeprecationLevel.WARNING
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
