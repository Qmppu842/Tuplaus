package io.qmppu842

import java.security.SecureRandom

object GameEngine {
    val secureRandom = SecureRandom()

    fun playGame(): Int {
        return secureRandom.nextInt(1, 14)
    }
}