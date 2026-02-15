package tools.jackson.module.kotlin.test.github

import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHub1089 {
    data class Bean(
        var id: Id
    )

    @JvmInline
    value class Id(val value: Long)

    @Test
    fun ok1() {
        val json = """{"id":3}"""
        val tree = defaultMapper.readTree(json)
        val data = defaultMapper.treeToValue(tree, Bean::class.java)
        assertEquals(3, data.id.value)
    }

    @Test
    fun ok2() {
        val json = """{"id":"3"}"""
        val data = defaultMapper.readValue(json, Bean::class.java)
        assertEquals(3, data.id.value)
    }

    @Test
    fun ko() {
        val json = """{"id":"3"}"""
        val tree = defaultMapper.readTree(json)
        val data = defaultMapper.treeToValue(tree, Bean::class.java)
        assertEquals(3, data.id.value)
    }
}
