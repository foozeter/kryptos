package com.hayashibambi.kryptos.console.ciphermachine.substitution

import com.hayashibambi.kryptos.console.ciphermachine.CipherMachine

interface SubstitutionCipherMachine: CipherMachine {
    fun isPrefixCode(): Boolean
}