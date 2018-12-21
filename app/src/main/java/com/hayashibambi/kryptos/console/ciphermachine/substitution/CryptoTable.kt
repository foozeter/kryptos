package com.hayashibambi.kryptos.console.ciphermachine.substitution

class CryptoTable {

    private val p2c = mutableMapOf<String, String>()
    private val c2p = mutableMapOf<String, String>()

    val minPlainWordLength
        get() = p2c.minBy { it.key.length }?.key?.length ?: 0

    val maxPlainWordLength
        get() = p2c.maxBy { it.key.length }?.key?.length ?: 0

    val minCipherWordLength
        get() = c2p.minBy { it.key.length }?.key?.length ?: 0

    val maxCipherWordLength
        get() = c2p.maxBy { it.key.length }?.key?.length ?: 0

    val plainWords
        get() = p2c.keys.toSet()

    val cipherWords
        get() = c2p.keys.toSet()

    fun registerWords(plain: String, cipher: String) =
        if (!p2c.containsKey(plain) &&
            !c2p.containsKey(cipher)) {
            p2c[plain] = cipher
            c2p[cipher] = plain
            true
        } else false

    fun unregisterWords(plain: String, cipher: String) =
        if (p2c.containsKey(plain) &&
            c2p.containsKey(cipher)) {
            p2c.remove(plain)
            c2p.remove(cipher)
            true
        } else false

    fun containsWords(plain: String, cipher: String)
            = containsPlainWords(plain)
            && containsCipherWords(cipher)

    fun containsPlainWords(plain: String)
            = p2c.containsKey(plain)

    fun containsCipherWords(cipher: String)
            = c2p.containsKey(cipher)

    fun encryptWord(plain: String) = p2c[plain]
    
    fun decryptWord(cipher: String) = c2p[cipher]
    
    fun clear() {
        p2c.clear()
        c2p.clear()
    }
}