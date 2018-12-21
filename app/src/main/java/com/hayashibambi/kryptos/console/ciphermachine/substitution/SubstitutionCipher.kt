package com.hayashibambi.kryptos.console.ciphermachine.substitution

open class SubstitutionCipher: SubstitutionCipherMachine {

    private val table = Table()

    override fun encrypt(text: String): String? {
        if (text.isEmpty()) return null
        var cursor = 0
        val cipherText = StringBuilder()
        while (cursor < text.length) {
            val encryptable = table.findEncryptableWordIn(text, cursor)
            if (encryptable != null) {
                cipherText.append(table.encrypt(encryptable))
                cursor += encryptable.length
            } else return null
        }

        return if (cipherText.isNotEmpty()) cipherText.toString()
        else null
    }

    override fun decrypt(text: String): String? {
        if (text.isEmpty()) return null
        var cursor = 0
        val plainText = StringBuilder()
        while (cursor < text.length) {
            val decryptable = table.findDecryptableWordIn(text, cursor)
            if (decryptable != null) {
                plainText.append(table.decrypt(decryptable))
                cursor += decryptable.length
            } else return null
        }

        return if (plainText.isNotEmpty()) plainText.toString()
        else null
    }

    override fun isPrefixCode(): Boolean {
        val cipherWords = table.sortedCipherWords.sortedBy { it.length }
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