package com.hayashibambi.kryptos.console.ciphermachine

import com.hayashibambi.kryptos.console.ciphermachine.substitution.SubstitutionCipher
import org.junit.Assert.assertEquals
import org.junit.Test

class SubstitutionCipherTest {

    @Test
    fun encrypt() {
        val cm = SubstitutionCipher().apply {
            putPair("abc", "0")
            putPair("def", "1")
            putPair("ghi", "2")
            putPair("jkl", "3")
        }

        var result = cm.encrypt("abcdefghijkl")
        assertEquals("0123", result)

        result = cm.encrypt("abcdav")
        assertEquals(null, result)

        result = cm.encrypt("abcabcabc")
        assertEquals("000", result)

        cm.apply {
            clearTable()
            putPair("abc", "0")
            putPair("abcdef", "1")
            putPair("abcdefghi", "2")
            putPair("abcdefghijkl", "3")
        }

        result = cm.encrypt("abcdef")
        assertEquals("1", result)

        result = cm.encrypt("abcdefghi")
        assertEquals("2", result)

        result = cm.encrypt("abcdefabc")
        assertEquals("10", result)

        result = cm.encrypt("abcdefghijklabcabcdef")
        assertEquals("301", result)
    }

    @Test
    fun decrypt() {
        val cm = SubstitutionCipher().apply {
            putPair("abc", "0")
            putPair("def", "1")
            putPair("ghi", "2")
            putPair("jkl", "3")
        }

        var result = cm.decrypt("012")
        assertEquals("abcdefghi", result)

        result = cm.decrypt("30")
        assertEquals("jklabc", result)

        result = cm.decrypt("abcde")
        assertEquals(null, result)

        cm.apply {
            clearTable()
            putPair("abc", "0")
            putPair("abcdef", "00")
            putPair("abcdefghi", "000")
        }
    }

    @Test
    fun isPrefixCode() {
        val cm = SubstitutionCipher().apply {
            putPair("a", "x")
            putPair("b", "y")
            putPair("c", "xy")
        }

        assertEquals(false, cm.isPrefixCode())

        cm.apply {
            clearTable()
            putPair("a", "x")
            putPair("b", "y")
            putPair("c", "XY")
        }

        assertEquals(true, cm.isPrefixCode())

        cm.apply {
            clearTable()
            putPair("a", "x")
            putPair("b", "y")
            putPair("c", "zxy")
        }

        assertEquals(true, cm.isPrefixCode())
    }
}