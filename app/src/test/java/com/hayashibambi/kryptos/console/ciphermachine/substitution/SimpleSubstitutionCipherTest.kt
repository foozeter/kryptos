package com.hayashibambi.kryptos.console.ciphermachine.substitution

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SimpleSubstitutionCipherTest {

    val sscm = SimpleSubstitutionCipher()

    @Before
    fun init() {
        sscm.apply {
            clearTable()
            register('x', '#')
            register('y', '&')
            register('z', '$')
        }
    }

    @Test
    fun encrypt() {
        assertEquals("&#$", sscm.encrypt("yxz"))
        assertEquals(null, sscm.encrypt("xyw"))
    }

    @Test
    fun decrypt() {
        assertEquals("xy", sscm.decrypt("#&"))
        assertEquals(null, sscm.decrypt("!#&"))
    }

    @Test
    fun isPrefixCode() {
        assertEquals(true, sscm.isPrefixCode())
        sscm.register('w', '$')
        assertEquals(true, sscm.isPrefixCode())
    }

}