package com.hayashibambi.kryptos.console.ciphermachine.substitution

import androidx.annotation.VisibleForTesting

class CaesarCipher: SimpleSubstitutionCipher() {

    companion object {
        private const val ALPHABET_COUNT = 26
        private const val DIGIT_COUNT = 10
        private const val DEFAULT_KEY = 3
    }

    var key = DEFAULT_KEY
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    init {
        invalidate()
    }

    private fun invalidate() {
        clearTable()
        for (a in '0'..'9') register(a, shift(a, key))
        for (a in 'a'..'z') register(a, shift(a, key))
        for (a in 'A'..'Z') register(a, shift(a, key))
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun shift(char: Char, distance: Int)
            = when {
        char.isLowerCase() -> {
            val shifted = char + distance % ALPHABET_COUNT
            when {
                'z' < shifted -> ('a' + shifted.toInt() - 'z' - 1).toChar()
                shifted < 'a' -> ('z' + shifted.toInt() - 'a' + 1).toChar()
                else -> shifted
            }
        }

        char.isUpperCase() -> {
            val shifted = char + distance % ALPHABET_COUNT
            when {
                'Z' < shifted -> ('A' + shifted.toInt() - 'Z' - 1).toChar()
                shifted < 'A' -> ('Z' + shifted.toInt() - 'A' + 1).toChar()
                else -> shifted
            }
        }

        char.isDigit() -> {
            val shifted = char + distance % DIGIT_COUNT
            when {
                '9' < shifted -> ('0' + shifted.toInt() - '9' - 1).toChar()
                shifted < '0' -> ('9' + shifted.toInt() - '0' + 1).toChar()
                else -> shifted
            }
        }

        else -> throw IllegalArgumentException("not supported ($char)")
    }

    /**
     * Caesar Cipher does not encrypt/decrypt unsupported characters.
     */

    protected override fun encrypt(plain: Char): Char? {
        return super.encrypt(plain) ?: plain
    }

    protected override fun decrypt(cipher: Char): Char? {
        return super.decrypt(cipher) ?: cipher
    }
}
