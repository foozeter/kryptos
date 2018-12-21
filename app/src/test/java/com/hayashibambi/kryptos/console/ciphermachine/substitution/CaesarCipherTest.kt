package com.hayashibambi.kryptos.console.ciphermachine.substitution

import org.junit.Assert.assertEquals
import org.junit.Test

class CaesarCipherTest {

    @Test
    fun shift() {
        val caesar = CaesarCipher()
        assertEquals('d', caesar.shift('a', 3))
        assertEquals('d', caesar.shift('a', 29))
        assertEquals('y', caesar.shift('b', -3))
        assertEquals('y', caesar.shift('b', -29))

        assertEquals('D', caesar.shift('A', 3))
        assertEquals('D', caesar.shift('A', 29))
        assertEquals('Y', caesar.shift('B', -3))
        assertEquals('Y', caesar.shift('B', -29))

        assertEquals('2', caesar.shift('0', 2))
        assertEquals('1', caesar.shift('9', 12))
        assertEquals('7', caesar.shift('9', -2))
        assertEquals('8', caesar.shift('0', -12))
    }

    @Test
    fun encrypt() {
        val caesar = CaesarCipher()
        caesar.key = 3
        assertEquals("Fdhvdu/5341", caesar.encrypt("Caesar/2018"))
    }

    @Test
    fun decrypt() {
        val caesar = CaesarCipher()
        caesar.key = 3
        assertEquals("Caesar/2018", caesar.decrypt("Fdhvdu/5341"))
    }
}