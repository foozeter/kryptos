package com.hayashibambi.kryptos.console.ciphermachine.substitution

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
                encrypted = table.encrypt(word)
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
                decrypted = table.decrypt(word)
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

    fun register(plain: String, cipher: String)
            = table.register(plain, cipher)

    fun unregister(plain: String, cipher: String)
            = table.unregister(plain, cipher)

    fun clearTable()
            = table.clear()
}