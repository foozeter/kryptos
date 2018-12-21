package com.hayashibambi.kryptos.console.ciphermachine.substitution

import androidx.annotation.VisibleForTesting

class CaesarCipher: SimpleSubstitutionCipher() {

    companion object {
        private const val ALPHABET_COUNT = 26
        private const val DIGIT_COUNT = 10
        private const val DEFAULT_KEY = 3
    }

    private object Const {

        /**
         * The following are supported non-shift characters (UTF-16):
         *
         * U+0021 - U+002F,
         * U+003A - U+0040,
         * U+005B - U+0060,
         * U+007B - U+007E
         */
        val NON_SHIFT_CHARSET =
            mutableListOf<Char>()
                .add((' '..'/'))
                .add((':'..'@'))
                .add(('['..'`'))
                .add(('{'..'~'))
                .toList()

        /**
         * Only alphabets and digits are supported.
         */
        val SHIFT_CHARSET =
            mutableListOf<Char>()
                .add(('0'..'9'))
                .add(('a'..'z'))
                .add(('A'..'Z'))
                .toList()

        private fun MutableList<Char>.add(range: CharRange): MutableList<Char> {
            addAll(range)
            return this
        }
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
        for (a in Const.NON_SHIFT_CHARSET) putPair(a, a)
        for (a in Const.SHIFT_CHARSET) putPair(a, shift(a, key))
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
}
