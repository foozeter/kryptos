package com.hayashibambi.kryptos.console.ciphermachine.substitution

class Table {

    private val p2c = mutableMapOf<String, String>()
    private val c2p = mutableMapOf<String, String>()

    val minPlainWordLength; get() = sortedPlainWords.firstOrNull()?.length ?: 0
    val maxPlainWordLength; get() = sortedPlainWords.lastOrNull()?.length ?: 0
    val minCipherWordLength; get() = sortedCipherWords.firstOrNull()?.length ?: 0
    val maxCipherWordLength; get() = sortedCipherWords.lastOrNull()?.length ?: 0

    // Should be sorted by the length of elements
    var sortedPlainWords = emptySet<String>(); private set
    var sortedCipherWords = emptySet<String>(); private set

    fun register(plain: String, cipher: String) =
        if (!p2c.containsKey(plain) &&
            !c2p.containsKey(cipher)) {
            p2c[plain] = cipher
            c2p[cipher] = plain
            invalidate()
            true
        } else false

    fun unregister(plain: String, cipher: String) =
        if (p2c.containsKey(plain) &&
            c2p.containsKey(cipher)) {
            p2c.remove(plain)
            c2p.remove(cipher)
            invalidate()
            true
        } else false

    fun contains(plain: String, cipher: String)
            = containsPlainWord(plain)
            && containsCipherWord(cipher)

    fun containsPlainWord(plain: String)
            = p2c.containsKey(plain)

    fun containsCipherWord(cipher: String)
            = c2p.containsKey(cipher)

    fun encrypt(plain: String) = p2c[plain]
    
    fun decrypt(cipher: String) = c2p[cipher]
    
    fun clear() {
        p2c.clear()
        c2p.clear()
        invalidate()
    }

    fun findUnencryptablePartsIn(plainText: String): Set<String> {
        if (sortedPlainWords.isEmpty()) return setOf(plainText)
        val unencryptable = StringBuilder()
        val unencryptables = mutableListOf<String>()
        var cursor = 0
        while (cursor < plainText.length) {
            var notFound = true
            for (i in sortedPlainWords.lastIndex() downTo 0) {
                val plain = sortedPlainWords.elementAt(i)
                if (plainText.startsWith(plain, cursor)) {
                    cursor += plain.length
                    notFound = false
                    if (unencryptable.isNotEmpty()) {
                        unencryptables.add(unencryptable.toString())
                        unencryptable.delete(0, unencryptable.length)
                    }
                    break
                }
            }

            if (notFound) {
                unencryptable.append(plainText[cursor])
                ++cursor
            }
        }

        if (unencryptable.isNotEmpty())
            unencryptables.add(unencryptable.toString())

        return if (unencryptables.isEmpty()) emptySet()
        else unencryptables.toSet()
    }

    fun findEncryptableWordIn(
        plainText: String,
        startIndex: Int): String? {
        for (length in maxPlainWordLength downTo minPlainWordLength) {
            if (plainText.length < startIndex + length) continue
            val substr = plainText.substring(startIndex, startIndex + length)
            if (encrypt(substr) != null) return substr
        }
        return null
    }

    fun findDecryptableWordIn(
        cipherText: String,
        startIndex: Int): String? {
        for (length in maxCipherWordLength downTo minCipherWordLength) {
            if (cipherText.length < startIndex + length) continue
            val substr = cipherText.substring(startIndex, startIndex + length)
            if (decrypt(substr) != null) return substr
        }
        return null
    }

    private fun invalidate() {
        sortedPlainWords = p2c.keys.asSequence().sortedBy { it.length }.toSet()
        sortedCipherWords = c2p.keys.asSequence().sortedBy { it.length }.toSet()
    }

    private fun Set<String>.lastIndex() = size - 1
}