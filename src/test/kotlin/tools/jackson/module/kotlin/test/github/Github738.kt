package tools.jackson.module.kotlin.test.github

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import tools.jackson.databind.exc.MismatchedInputException
import tools.jackson.module.kotlin.defaultMapper
import tools.jackson.module.kotlin.readValue

class Github738 {
    data class D(@JsonSetter(nulls = Nulls.FAIL) val v: Int)

    @Test
    fun test() {
        // nulls = FAIL is reflected if it is primitive and missing
        assertThrows(MismatchedInputException::class.java) { defaultMapper.readValue<D>("{}") }
    }
}
