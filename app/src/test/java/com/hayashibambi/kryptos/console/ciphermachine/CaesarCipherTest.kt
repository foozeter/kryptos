package com.hayashibambi.kryptos.console.ciphermachine

import org.junit.Assert.assertEquals
import org.junit.Test

class CaesarCipherTest {

    @Test
    fun encrypt() {
        val caesar = CaesarCipher()
        caesar.key = 1
        assertEquals("Dbftbs", caesar.encrypt("Caesar"))
        assertEquals("2008", caesar.encrypt("1997"))
        assertEquals("Dbftbs 2008", caesar.encrypt("Caesar 1997"))
    }

    @Test
    fun decrypt() {
        val caesar = CaesarCipher()
        caesar.key = 1
        assertEquals("Caesar", caesar.decrypt("Dbftbs"))
        assertEquals("1997", caesar.decrypt("2008"))
        assertEquals("Caesar 1997", caesar.decrypt("Dbftbs 2008"))
    }

    @Test
    fun shift() {
        val caesar = CaesarCipher()
        assertEquals('d', caesar.shift('a', 3))
        assertEquals('d', caesar.shift('a', 29))
        assertEquals('b', caesar.shift('y', 3))
        assertEquals('y', caesar.shift('b', -3))
        assertEquals('y', caesar.shift('b', -29))
        assertEquals('D', caesar.shift('A', 3))
        assertEquals('D', caesar.shift('A', 29))
        assertEquals('B', caesar.shift('Y', 3))
        assertEquals('Y', caesar.shift('B', -3))
        assertEquals('Y', caesar.shift('B', -29))
        assertEquals('2', caesar.shift('0', 2))
        assertEquals('0', caesar.shift('9', 1))
        assertEquals('8', caesar.shift('9', -1))
        assertEquals('2', caesar.shift('0', 12))
    }

}