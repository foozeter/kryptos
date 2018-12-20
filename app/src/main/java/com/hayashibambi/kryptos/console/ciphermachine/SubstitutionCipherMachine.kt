package com.hayashibambi.kryptos.console.ciphermachine

interface SubstitutionCipherMachine: CipherMachine {
    fun isPrefixCode(): Boolean
}