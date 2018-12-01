package de.kaleidox.util

import org.junit.Test

import static de.kaleidox.util.LogicalOperator.AND
import static de.kaleidox.util.LogicalOperator.NAND
import static de.kaleidox.util.LogicalOperator.OR
import static de.kaleidox.util.LogicalOperator.XOR

class LogicalOperatorTest {
    @Test
    void testAnd() {
        assert AND.test([true, true, true])
        assert !AND.test([true, false, true])
    }

    @Test
    void testOr() {
        assert OR.test([false, true, false])
        assert OR.test([false, true, true])
        assert !OR.test([false, false, false])
    }

    @Test
    void testNot() {
        assert NAND.test([false, false, false])
        assert !NAND.test([false, true, false])
        assert !NAND.test([true, true, true])
    }

    @Test
    void testXor() {
        assert XOR.test([true, false, false])
        assert XOR.test([false, false, true])
        assert !XOR.test([false, true, true])
        assert !XOR.test([true, true, true])
    }
}
