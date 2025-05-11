package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.databind.MapperFeature
import tools.jackson.module.kotlin.jacksonMapperBuilder
import tools.jackson.module.kotlin.readValue
import kotlin.test.assertEquals

class TestGithub47 {

    class ConfigItem(val configItemId: String)

    @Test
    fun testCaseInsensitivePropertyNames() {
        val mapper = jacksonMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build()

        val jsonWithMismtachedPropertyName = """
                    {
                        "configItemID": "test"
                    }
                   """

        val item: ConfigItem = mapper.readValue(jsonWithMismtachedPropertyName)
        assertEquals("test", item.configItemId)
    }
}
