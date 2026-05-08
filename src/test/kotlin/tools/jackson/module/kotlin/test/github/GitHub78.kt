package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.PropertyAccessor
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper

class GitHub78 {
    enum class JacksonTest(private val value: Int) {
        TEST(0), TEST2(1);

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromInt(value: Int): JacksonTest {
                return entries.find { it.value == value } ?: TEST
            }
        }
    }

    enum class JacksonTest2(private val value: Int) {
        TEST(0), TEST2(1);

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromInt(intValue: Int): JacksonTest2 {
                return entries.find { it.value == intValue } ?: TEST
            }
        }
    }

    @Test
    fun jacksonTest1_customMapper() {
        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .changeDefaultVisibility { v ->
                v.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .withVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            }
            .build()

        val deserialized = mapper.readValue<JacksonTest>("1")
        assertEquals(JacksonTest.TEST2, deserialized)
    }

    @Test
    fun jacksonTest1_defaultMapper() {
        val mapper = defaultMapper

        val deserialized = mapper.readValue<JacksonTest>("1")
        assertEquals(JacksonTest.TEST2, deserialized)
    }

    @Test
    fun jacksonTest2_customMapper() {
        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .changeDefaultVisibility { v ->
                v.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .withVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            }
            .build()
        
        val deserialized = mapper.readValue<JacksonTest2>("1")
        assertEquals(JacksonTest2.TEST2, deserialized)
    }

    @Test
    fun jacksonTest2_defaultMapper() {
        val mapper = defaultMapper
        
        val deserialized = mapper.readValue<JacksonTest2>("1")
        assertEquals(JacksonTest2.TEST2, deserialized)
    }
}
