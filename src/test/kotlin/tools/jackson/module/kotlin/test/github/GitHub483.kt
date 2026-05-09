package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GitHub483 {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TestYml(
        val name: String?
    )

    @Test
    fun testYamlIgnoreUnknownWithListBefore() {
        val mapper = YAMLMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()

        val yaml = """
            ports:
              - 3306
            name: containerNameTest
        """.trimIndent()

        val result = mapper.readValue<TestYml>(yaml)

        assertEquals("containerNameTest", result.name)
    }

    @Test
    fun testYamlIgnoreUnknownWithListAfter() {
        val mapper = YAMLMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()

        val yaml = """
            name: containerNameTest
            ports:
              - 3306
        """.trimIndent()

        val result = mapper.readValue<TestYml>(yaml)

        assertEquals("containerNameTest", result.name)
    }
}
