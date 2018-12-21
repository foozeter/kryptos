package com.hayashibambi.kryptos.console.ciphermachine.substitution

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class CryptoTableTest {

    val table = CryptoTable()

    @Before
    fun init() {
        table.apply {
            table.clear()
            registerWords("dog", "01")
            registerWords("cat man", "110")
            registerWords("rabbit", "11010")
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
        var result = table.registerWords("a", "0")
        assertEquals(true, result)
        assertEquals("0", table.encryptWord("a"))

        result = table.registerWords("a", "1")
        assertEquals(false, result)
        assertEquals("0", table.encryptWord("a"))
    }

    @Test
    fun unregisterWords() {
        val beforePlainWords = table.plainWords
        val beforeCipherWords = table.cipherWords
        var result = table.unregisterWords("ant", "01")
        assertEquals(false, result)
        assertEquals(true, beforePlainWords == table.plainWords)
        assertEquals(true, beforeCipherWords == table.cipherWords)

        result = table.unregisterWords("dog", "01")
        assertEquals(true, result)
        assertNotEquals(beforePlainWords, table.plainWords)
        assertNotEquals(beforeCipherWords, table.cipherWords)
    }

    @Test
    fun containsWords() {
        assertEquals(true, table.containsWords("dog", "01"))
        assertEquals(false, table.containsWords("ant", "010"))
    }

    @Test
    fun containsPlainWords() {
        assertEquals(true, table.containsPlainWords("dog"))
        assertEquals(false, table.containsPlainWords("ant"))
    }

    @Test
    fun containsCipherWords() {
        assertEquals(true, table.containsCipherWords("01"))
        assertEquals(false, table.containsCipherWords("0001"))
    }

    @Test
    fun encryptWord() {
        assertEquals("01", table.encryptWord("dog"))
        assertEquals(null, table.encryptWord("ant"))
    }

    @Test
    fun decryptWord() {
        assertEquals("rabbit", table.decryptWord("11010"))
        assertEquals(null, table.decryptWord("111"))
    }

    @Test
    fun clear() {
        table.clear()
        assertEquals(0, table.plainWords.size)
        assertEquals(0, table.cipherWords.size)
    }
}