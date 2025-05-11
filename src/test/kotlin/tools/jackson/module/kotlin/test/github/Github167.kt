package tools.jackson.module.kotlin.test.github

import org.junit.jupiter.api.Test
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.defaultMapper
import java.util.function.IntSupplier

class TestGithub167 {
    val samObject = IntSupplier { 42 }

    val answer = 42
    val samObjectSynthetic = IntSupplier { answer }

    @Test
    fun withKotlinExtension() {
        defaultMapper.writeValueAsString(samObject)
    }

    @Test
    fun withKotlinExtension_Synthetic() {
        defaultMapper.writeValueAsString(samObjectSynthetic)
    }


    @Test
    fun withoutKotlinExtension() {
        ObjectMapper().writeValueAsString(samObject)
    }

    @Test
    fun withoutKotlinExtension_Synthetic() {
        ObjectMapper().writeValueAsString(samObjectSynthetic)
    }
}
