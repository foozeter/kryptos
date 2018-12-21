package com.hayashibambi.kryptos.console.ciphermachine

import com.hayashibambi.kryptos.console.ciphermachine.substitution.SimpleSubstitutionCipher
import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleSubstitutionCipherTest {

    @Test
    fun encrypt() {
        val cm = SimpleSubstitutionCipher()
        cm.apply {
            putPair('a', '0')
            putPair('b', '1')
            putPair('c', '2')
        }

        assertEquals("012", cm.encrypt("abc"))
        assertEquals(null, cm.encrypt("xyz"))
    }

    @Test
    fun decrypt() {
        val cm = SimpleSubstitutionCipher()
        cm.apply {
            putPair('a', '0')
            putPair('b', '1')
            putPair('c', '2')
        }

        assertEquals("abc", cm.decrypt("012"))
        assertEquals(null, cm.decrypt("789"))
    }

    @Test
    fun isPrefixCode() {
        val cm = SimpleSubstitutionCipher()
        cm.apply {
            putPair('a', '0')
            putPair('b', '1')
            putPair('c', '2')
        }

        assertEquals(true, cm.isPrefixCode())

        cm.apply {
            clearTable()
            putPair('a', '0')
            // cannot put (b, 0), because '0' is already used
            putPair('b', '0')
            putPair('c', '2')
        }

        assertEquals(true, cm.isPrefixCode())

        cm.apply {
            clearTable()
            putPair('a', 'x')
            putPair('b', 'X')
        }

        assertEquals(true, cm.isPrefixCode())
    }
}