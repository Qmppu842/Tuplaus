package io.qmppu842.database

import io.qmppu842.GameEvent
import java.util.*

interface GameEventsDao {
    suspend fun newGame(playerIdent: UUID, time: Long, bet: Int, endCard: Int, winnings: Int, comboId: Int?): GameEvent?
    suspend fun getLastGameFromPlayer(playerIdentity: UUID): GameEvent?
}