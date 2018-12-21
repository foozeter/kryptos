package com.hayashibambi.kryptos.console.ciphermachine

import com.hayashibambi.kryptos.console.ciphermachine.substitution.SubstitutionCipher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SubstitutionCipherTest {

    val scm = SubstitutionCipher()

    @Before
    fun init() {
        scm.apply {
            clearTable()
            register("it", "000")
            register("'", "001")
            register("s", "010")
            register("a", "011")
            register("long", "100")
            register("way", "101")
            register("to", "110")
            register("the", "1110")
            register("top", "1111")
            register(" ", "-")
        }
    }

    @Test
    fun encrypt() {
        assertEquals(
            "000001010-011-100-101-110-1110-1111",
            scm.encrypt("it's a long way to the top"))

        assertEquals(null,
            scm.encrypt("it's a long way to the top " +
                    "(if you wanna rock 'n' roll)"))
    }

    @Test
    fun decrypt() {
        assertEquals(
            "it's a long way to the top",
            scm.decrypt("000001010-011-100-101-110-1110-1111"))

        assertEquals(null,
            scm.decrypt("000001010-011-100-101-110-1110-1111-xyz"))
    }

    @Test
    fun isPrefixCode() {
        assertEquals(true, scm.isPrefixCode())
        scm.unregister("to", "110")
        scm.register("to", "111")
        assertEquals(false, scm.isPrefixCode())
    }
}