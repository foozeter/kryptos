package com.hayashibambi.kryptos.console.ciphermachine.substitution

import androidx.annotation.VisibleForTesting

open class SimpleSubstitutionCipher:
    SubstitutionCipherMachine {

    private val table = Table()

    final override fun encrypt(text: String): String? {
        val cipherText = StringBuilder()
        text.forEach {
            val encrypted = encrypt(it)
            if (encrypted != null) {
                cipherText.append(encrypted)
            } else return null
        }

        return if (cipherText.isEmpty()) null
        else cipherText.toString()
    }

    final override fun decrypt(text: String): String? {
        val plainText = StringBuilder()
        text.forEach {
            val decrypted = decrypt(it)
            if (decrypted != null) {
                plainText.append(decrypted)
            } else return null
        }

        return if (plainText.isEmpty()) null
        else plainText.toString()
    }

    final override fun isPrefixCode(): Boolean
            = table.sortedCipherWords.areElementsUnique()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun register(plain: Char, cipher: Char)
            = table.register(plain.toString(), cipher.toString())

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun unregister(plain: Char, cipher: Char)
            = table.unregister(plain.toString(), cipher.toString())

    fun clearTable() = table.clear()

    protected open fun encrypt(plain: Char)
            = table.encrypt(plain.toString())?.get(0)

    protected open fun decrypt(cipher: Char)
            = table.decrypt(cipher.toString())?.get(0)

    private fun Set<String>.areElementsUnique(): Boolean {
        for (i in 0 until size) {
            val ei = elementAt(i)
            for (j in (i + 1) until size) {
                if (ei == elementAt(j)) return false
            }
        }

        return true
    }
}