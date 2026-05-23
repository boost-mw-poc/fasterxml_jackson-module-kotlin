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
import java.time.Instant as JavaInstant
import kotlin.time.Instant as KotlinInstant

@OptIn(ExperimentalTime::class)
class InstantTests {
    private val mapperBuilder = JsonMapper.builder()
        .addModule(kotlinModule { enable(KotlinFeature.UseJavaInstantConversion) })

    @Test
    fun `should serialize Kotlin Instant to ISO 8601 format`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(KotlinInstant.parse("2023-06-20T14:00:00Z"))

        assertEquals("\"2023-06-20T14:00:00Z\"", result)
    }

    @Test
    fun `should serialize Kotlin Instant to epoch seconds with nanoseconds`() {
        val mapper = mapperBuilder
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()

        val result = mapper.writeValueAsString(KotlinInstant.parse("2023-06-20T14:00:00.123Z"))

        assertEquals("1687269600.123000000", result)
    }

    @Test
    fun `should serialize Kotlin Instant to milliseconds`() {
        val mapper = mapperBuilder
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .build()

        val result = mapper.writeValueAsString(KotlinInstant.parse("2023-06-20T14:00:00.123Z"))

        assertEquals("1687269600123", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside list`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(
            listOf(
                KotlinInstant.parse("2023-06-20T14:00:00Z"),
                KotlinInstant.parse("2024-06-20T14:00:00Z")
            )
        )

        assertEquals("""["2023-06-20T14:00:00Z","2024-06-20T14:00:00Z"]""", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside map`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(
            mapOf(
                "a" to KotlinInstant.parse("2023-06-20T14:00:00Z"),
                "b" to KotlinInstant.parse("2024-06-20T14:00:00Z")
            )
        )

        assertEquals("""{"a":"2023-06-20T14:00:00Z","b":"2024-06-20T14:00:00Z"}""", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside data class`() {
        val mapper = mapperBuilder.build()

        val result = mapper.writeValueAsString(Wrapper(KotlinInstant.parse("2023-06-20T14:00:00Z")))

        assertEquals("""{"time":"2023-06-20T14:00:00Z"}""", result)
    }

    @Test
    fun `should serialize Kotlin Instant inside data class using mixin`() {
        val mapper = mapperBuilder
            .addMixIn(Wrapper::class.java, WrapperMixin::class.java)
            .build()

        val result = mapper.writeValueAsString(Wrapper(KotlinInstant.parse("2023-06-20T14:00:00Z")))

        assertEquals("""{"time":"2023-06-20 14:00"}""", result)
    }

    @Test
    fun `should serialize Kotlin Instant exactly as Java Instant`() {
        val mapper = mapperBuilder.build()

        val jdto = JDTO()
        val kdto = KDTO()

        assertEquals(mapper.writeValueAsString(jdto), mapper.writeValueAsString(kdto))
    }

    @Test
    fun `should deserialize Kotlin Instant from ISO 8601 format`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<KotlinInstant>("\"2023-06-20T14:00:00Z\"")

        assertEquals(KotlinInstant.parse("2023-06-20T14:00:00Z"), result)
    }

    @Test
    fun `should deserialize Kotlin Instant from epoch seconds`() {
        val mapper = mapperBuilder
            .build()

        val result = mapper.readValue<KotlinInstant>("1778576404")

        assertEquals(KotlinInstant.fromEpochSeconds(1778576404), result)
    }

    @Test
    fun `should deserialize Kotlin Instant from epoch seconds with milliseconds`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<KotlinInstant>("1778576404.123")

        assertEquals(KotlinInstant.fromEpochMilliseconds(1778576404123), result)
    }

    @Test
    fun `should deserialize Kotlin Instant inside list`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<List<KotlinInstant>>("""["2023-06-20T14:00:00Z","2024-06-20T14:00:00Z"]""")

        assertContentEquals(
            listOf(KotlinInstant.parse("2023-06-20T14:00:00Z"), KotlinInstant.parse("2024-06-20T14:00:00Z")),
            result
        )
    }

    @Test
    fun `should deserialize Kotlin Instant inside map`() {
        val mapper = mapperBuilder.build()

        val result = mapper
            .readValue<Map<String, KotlinInstant>>("""{"a":"2023-06-20T14:00:00Z","b":"2024-06-20T14:00:00Z"}""")

        assertEquals(KotlinInstant.parse("2023-06-20T14:00:00Z"), result["a"])
        assertEquals(KotlinInstant.parse("2024-06-20T14:00:00Z"), result["b"])
    }

    @Test
    fun `should deserialize Kotlin Instant inside data class`() {
        val mapper = mapperBuilder.build()

        val result = mapper.readValue<Wrapper>("""{"time":"2023-06-20T14:00:00Z"}""")

        assertEquals(KotlinInstant.parse("2023-06-20T14:00:00Z"), result.time)
    }

    @Test
    fun `should deserialize Kotlin Instant inside data class using mixin`() {
        val mapper = mapperBuilder
            .addMixIn(Wrapper::class.java, WrapperMixin::class.java)
            .build()

        val result = mapper.readValue<Wrapper>("""{"time":"2023-06-20 14:00"}""")

        assertEquals(KotlinInstant.parse("2023-06-20T14:00:00Z"), result.time)
    }

    data class Wrapper(
        val time: KotlinInstant
    )

    abstract class WrapperMixin(
        @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
        val time: KotlinInstant
    )

    data class JDTO(
        val plain: JavaInstant = JavaInstant.parse("2023-06-20T14:00:00Z"),
        val optPlain: JavaInstant? = JavaInstant.parse("2023-06-20T14:00:00Z"),
        @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
        val shapeAnnotation: JavaInstant = JavaInstant.parse("2023-06-20T14:00:00Z"),
        @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
        val optShapeAnnotation: JavaInstant? = JavaInstant.parse("2023-06-20T14:00:00Z"),
    )

    data class KDTO(
        val plain: KotlinInstant = KotlinInstant.parse("2023-06-20T14:00:00Z"),
        val optPlain: KotlinInstant? = KotlinInstant.parse("2023-06-20T14:00:00Z"),
        @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
        val shapeAnnotation: KotlinInstant = KotlinInstant.parse("2023-06-20T14:00:00Z"),
        @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
        val optShapeAnnotation: KotlinInstant? = KotlinInstant.parse("2023-06-20T14:00:00Z"),
    )
}
