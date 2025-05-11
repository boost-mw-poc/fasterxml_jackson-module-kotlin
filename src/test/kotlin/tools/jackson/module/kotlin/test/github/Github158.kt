package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals

class TestGithub158 {
    enum class SampleImpl constructor(override val value: String): Sample {
        One("oney"),
        Two("twoey")
    }

    interface Sample {
        val value: String
    }

    data class SampleContainer(@JsonDeserialize(`as` = SampleImpl::class) val sample: Sample)

    @Test
    fun testEnumSerDeser() {
        val original = SampleContainer(SampleImpl.One)

        val json = defaultMapper.writeValueAsString(original)
//        println(json)
        val obj = defaultMapper.readValue<SampleContainer>(json)
        assertEquals(original, obj)
    }
}
