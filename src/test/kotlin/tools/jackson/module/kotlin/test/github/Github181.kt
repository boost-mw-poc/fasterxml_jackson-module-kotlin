package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonCreator
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals

class TestGithub181 {
    enum class HealthStatus {
        FAILED,
        OK
    }

    data class HealthStatusMap @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor (val statuses: Map<String, HealthStatus>)
        : Map<String, HealthStatus> by statuses {

        fun isPassing() = statuses.all { (_, v) -> v == HealthStatus.OK }
    }

    @Test
    fun testReflectionExceptionOnDelegatedMap() {
        val testInstance = HealthStatusMap(mapOf("failed" to HealthStatus.FAILED, "okey dokey" to HealthStatus.OK))
        val json = defaultMapper.writeValueAsString(testInstance)
        assertEquals("{\"failed\":\"FAILED\",\"okey dokey\":\"OK\"}", json)
        val newInstance = defaultMapper.readValue<HealthStatusMap>(json)
        assertEquals(testInstance, newInstance)
    }
}
