package com.hayashibambi.kryptos.console.ciphermachine

interface CipherMachine {
    fun encrypt(text: String): String?
    fun decrypt(text: String): String?
}