package com.hayashibambi.kryptos.console.ciphermachine

open class SubstitutionCipher: SubstitutionCipherMachine {

    private val table = Table()

    override fun encrypt(text: String): String? {
        if (text.isEmpty()) return null
        val minWordLength = table.minPlainWordLength
        val maxWordLength = table.maxPlainWordLength
        val cipherText = StringBuilder()
        var cursor = 0
        while (cursor < text.length) {

            var encrypted: String? = null
            for (i in maxWordLength downTo  minWordLength) {
                if (text.length < cursor + i) continue
                val word = text.substring(cursor, cursor + i)
                encrypted = table.encryptWord(word)
                if (encrypted != null) {
                    cursor += word.length
                    break
                }
            }

            if (encrypted != null) {
                cipherText.append(encrypted)
            } else {
                return null
            }
        }

        return if (cipherText.isNotEmpty()) cipherText.toString() else null
    }

    override fun decrypt(text: String): String? {
        if (text.isEmpty()) return null
        val minWordLength = table.minCipherWordLength
        val maxWordLength = table.maxCipherWordLength
        val plainText = StringBuilder()
        var cursor = 0
        while (cursor < text.length) {

            var decrypted: String? = null
            for (i in maxWordLength downTo  minWordLength) {
                if (text.length < cursor + i) continue
                val word = text.substring(cursor, cursor + i)
                decrypted = table.decryptWord(word)
                if (decrypted != null) {
                    cursor += word.length
                    break
                }
            }

            if (decrypted != null) {
                plainText.append(decrypted)
            } else {
                return null
            }
        }

        return if (plainText.isNotEmpty()) plainText.toString() else null
    }

    override fun isPrefixCode(): Boolean {
        val cipherWords = table.cipherWords.sortedBy { it.length }
        for (i in 0 until cipherWords.size) {
            val prefix = cipherWords[i]
            for (j in (i + 1) until cipherWords.size) {
                if (cipherWords[j].startsWith(prefix)) {
                    return false
                }
            }
        }

        return true
    }

    fun putPair(plain: String, cipher: String)
            = table.put(plain, cipher)

    fun removePair(plain: String, cipher: String)
            = table.remove(plain, cipher)

    fun findPairByPlainWord(plain: String)
            = table.findPairByPlainWord(plain)

    fun findPairByCipherWord(cipher: String)
            = table.findPairByCipherWord(cipher)

    fun findPairBy(plain: String, cipher: String)
            = table.findPairBy(plain, cipher)

    fun clearTable()
            = table.clear()

    private class Table {

        // Pair.first is a plain word, Pair.second is a cipher word.
        private val words = mutableListOf<Pair<String, String>>()

        val minPlainWordLength
            get() = words.minBy { it.first.length }?.first?.length ?: 0

        val maxPlainWordLength
            get() = words.maxBy { it.first.length }?.first?.length ?: 0

        val minCipherWordLength
            get() = words.minBy { it.second.length }?.second?.length ?: 0

        val maxCipherWordLength
            get() = words.maxBy { it.second.length }?.second?.length ?: 0

        val plainWords
            get() = words.extract { it.first }

        val cipherWords
            get() = words.extract { it.second }

        fun encryptWord(word: String): String?
                = words.find { it.first == word }?.second

        fun decryptWord(word: String): String?
                = words.find { it.second == word }?.first

        fun put(plain: String, cipher: String): Boolean {
            if (plain.isEmpty() || cipher.isEmpty()) return false
            val exists = words.find { it.first == plain || it.second == cipher }
            return if (exists != null) false
            else words.add(Pair(plain, cipher))
        }

        fun remove(plain: String, cipher: String): Boolean {
            val index = words.indexOfFirst { it.first == plain || it.second == cipher }
            return if (index == -1) false
            else {
                words.removeAt(index)
                true
            }
        }

        fun clear() = words.clear()

        fun findPairByPlainWord(plain: String)
                = words.find { it.first == plain }

        fun findPairByCipherWord(cipher: String)
                = words.find { it.second == cipher }

        fun findPairBy(plain: String, cipher: String)
                = words.find { it.first == plain && it.second == cipher }

        fun <T, U> MutableList<T>.extract(extractor: (element: T) -> U): MutableList<U> {
            val new = mutableListOf<U>()
            forEach { new.add(extractor(it)) }
            return new
        }
    }
}