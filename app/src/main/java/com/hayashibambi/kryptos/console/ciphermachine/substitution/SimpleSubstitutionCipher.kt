package com.hayashibambi.kryptos.console.ciphermachine.substitution

open class SimpleSubstitutionCipher:
    SubstitutionCipherMachine {

    private val table = mutableListOf<Pair<Char, Char>>()

    override fun encrypt(text: String): String? {
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

    override fun decrypt(text: String): String? {
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

    override fun isPrefixCode(): Boolean
            = table.extract { it.second }.areElementsUnique()

    fun putPair(plain: Char, cipher: Char) {
        val exists = table.find { it.first == plain || it.second == cipher }
        if (exists == null) table.add(Pair(plain, cipher))
    }

    fun removePair(plain: Char, cipher: Char) {
        val index = table.indexOfFirst { it.first == plain || it.second == cipher }
        if (index != -1) table.removeAt(index)
    }

    fun clearTable() = table.clear()

    private fun encrypt(plain: Char)
            = table.find { it.first == plain }?.second

    private fun decrypt(cipher: Char)
            = table.find { it.second == cipher }?.first

    private fun MutableList<*>.areElementsUnique(): Boolean {
        for (i in 0 until size) {
            val ei = get(i)
            for (j in (i + 1) until size) {
                if (ei == get(j)) return false
            }
        }

        return true
    }

    private fun <T, U> MutableList<T>.extract(extractor: (element: T) -> U): MutableList<U> {
        val new = mutableListOf<U>()
        forEach { new.add(extractor(it)) }
        return new
    }
}