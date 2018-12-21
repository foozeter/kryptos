package com.hayashibambi.kryptos.console.ciphermachine.substitution

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class TableTest {

    val table = Table()

    @Before
    fun init() {
        table.apply {
            table.clear()
            register("dog", "01")
            register("cat man", "110")
            register("rabbit", "11010")
        }
    }

    @Test
    fun getMinPlainWordLength() {
        assertEquals(3, table.minPlainWordLength)
    }

    @Test
    fun getMaxPlainWordLength() {
        assertEquals(7, table.maxPlainWordLength)
    }

    @Test
    fun getMinCipherWordLength() {
        assertEquals(2, table.minCipherWordLength)
    }

    @Test
    fun getMaxCipherWordLength() {
        assertEquals(5, table.maxCipherWordLength)
    }

    @Test
    fun getPlainWords() {
        val plains = setOf("dog", "cat man", "rabbit")
        assertEquals(plains, table.plainWords)
    }

    @Test
    fun getCipherWords() {
        val plains = setOf("01", "110", "11010")
        assertEquals(plains, table.cipherWords)
    }

    @Test
    fun registerWords() {
        table.clear()
        var result = table.register("a", "0")
        assertEquals(true, result)
        assertEquals("0", table.encrypt("a"))

        result = table.register("a", "1")
        assertEquals(false, result)
        assertEquals("0", table.encrypt("a"))
    }

    @Test
    fun unregisterWords() {
        val beforePlainWords = table.plainWords
        val beforeCipherWords = table.cipherWords
        var result = table.unregister("ant", "01")
        assertEquals(false, result)
        assertEquals(true, beforePlainWords == table.plainWords)
        assertEquals(true, beforeCipherWords == table.cipherWords)

        result = table.unregister("dog", "01")
        assertEquals(true, result)
        assertNotEquals(beforePlainWords, table.plainWords)
        assertNotEquals(beforeCipherWords, table.cipherWords)
    }

    @Test
    fun containsWords() {
        assertEquals(true, table.contains("dog", "01"))
        assertEquals(false, table.contains("ant", "010"))
    }

    @Test
    fun containsPlainWords() {
        assertEquals(true, table.containsPlainWord("dog"))
        assertEquals(false, table.containsPlainWord("ant"))
    }

    @Test
    fun containsCipherWords() {
        assertEquals(true, table.containsCipherWord("01"))
        assertEquals(false, table.containsCipherWord("0001"))
    }

    @Test
    fun encryptWord() {
        assertEquals("01", table.encrypt("dog"))
        assertEquals(null, table.encrypt("ant"))
    }

    @Test
    fun decryptWord() {
        assertEquals("rabbit", table.decrypt("11010"))
        assertEquals(null, table.decrypt("111"))
    }

    @Test
    fun clear() {
        table.clear()
        assertEquals(0, table.plainWords.size)
        assertEquals(0, table.cipherWords.size)
    }
}