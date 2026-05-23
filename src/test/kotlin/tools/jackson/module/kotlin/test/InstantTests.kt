package tools.jackson.module.kotlin.test

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import org.junit.jupiter.api.Test
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class InstantTests {
    private val mapperBuilder = JsonMapper.builder()
        .addModule(kotlinModule { enable(KotlinFeature.UseJavaInstantConversion) })

    @Test
    fun `should serialize Kotlin Instant to ISO 8601 format`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(Instant.parse("2023-06-20T14:00:00Z"))

        assertEquals("\"2023-06-20T14:00:00Z\"", result)
    }

    @Test
    fun `should serialize Kotlin Instant to epoch seconds with nanoseconds`() {
        val mapper = mapperBuilder
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()

        val result = mapper.writeValueAsString(Instant.parse("2023-06-20T14:00:00.123Z"))

        assertEquals("1687269600.123000000", result)
    }

    @Test
    fun `should serialize Kotlin Instant to milliseconds`() {
        val mapper = mapperBuilder
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .build()

        val result = mapper.writeValueAsString(Instant.parse("2023-06-20T14:00:00.123Z"))

        assertEquals("1687269600123", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside list`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(listOf(Instant.parse("2023-06-20T14:00:00Z"), Instant.parse("2024-06-20T14:00:00Z")))

        assertEquals("""["2023-06-20T14:00:00Z","2024-06-20T14:00:00Z"]""", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside map`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(
            mapOf(
                "a" to Instant.parse("2023-06-20T14:00:00Z"),
                "b" to Instant.parse("2024-06-20T14:00:00Z")
            )
        )

        assertEquals("""{"a":"2023-06-20T14:00:00Z","b":"2024-06-20T14:00:00Z"}""", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside data class`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(Pojo(Instant.parse("2023-06-20T14:00:00Z")))

        assertEquals("""{"time":"2023-06-20T14:00:00Z"}""", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside data class using mixin`() {
        val mapper = mapperBuilder
            .addMixIn(Pojo::class.java, PojoMixin::class.java)
            .build()

        val result = mapper.writeValueAsString(Pojo(Instant.parse("2023-06-20T14:00:00Z")))

        assertEquals("""{"time":"2023-06-20 14:00"}""", result)
    }

    @Test
    fun `should deserialize Kotlin Instant from ISO 8601 format`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<Instant>("\"2023-06-20T14:00:00Z\"")

        assertEquals(Instant.parse("2023-06-20T14:00:00Z"), result)
    }

    @Test
    fun `should deserialize Kotlin Instant from epoch seconds`() {
        val mapper = mapperBuilder
            .build()

        val result = mapper.readValue<Instant>("1778576404")

        assertEquals(Instant.fromEpochSeconds(1778576404), result)
    }

    @Test
    fun `should deserialize Kotlin Instant from epoch seconds with milliseconds`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<Instant>("1778576404.123")

        assertEquals(Instant.fromEpochMilliseconds(1778576404123), result)
    }

    @Test
    fun `should deserialize Kotlin Instant inside list`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<List<Instant>>("""["2023-06-20T14:00:00Z","2024-06-20T14:00:00Z"]""")

        assertContentEquals(listOf(Instant.parse("2023-06-20T14:00:00Z"), Instant.parse("2024-06-20T14:00:00Z")), result)
    }

    @Test
    fun `should deserialize Kotlin Instant inside map`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<Map<String, Instant>>("""{"a":"2023-06-20T14:00:00Z","b":"2024-06-20T14:00:00Z"}""")

        assertEquals(Instant.parse("2023-06-20T14:00:00Z"), result["a"])
        assertEquals(Instant.parse("2024-06-20T14:00:00Z"), result["b"])
    }

    @Test
    fun `should deserialize Kotlin Instant inside data class`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<Pojo>("""{"time":"2023-06-20T14:00:00Z"}""")

        assertEquals(Instant.parse("2023-06-20T14:00:00Z"), result.time)
    }

    @Test
    fun `should deserialize Kotlin Instant inside data class using mixin`() {
        val mapper = mapperBuilder
            .addMixIn(Pojo::class.java, PojoMixin::class.java)
            .build()

        val result = mapper.readValue<Pojo>("""{"time":"2023-06-20 14:00"}""")

        assertEquals(Instant.parse("2023-06-20T14:00:00Z"), result.time)
    }

    data class Pojo(
        val time: Instant
    )

    abstract class PojoMixin(
        @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
        val time: Instant
    )
}
