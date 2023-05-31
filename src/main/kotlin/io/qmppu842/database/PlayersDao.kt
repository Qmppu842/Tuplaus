package io.qmppu842.database

import io.qmppu842.Player
import java.util.UUID

interface PlayersDao {
    suspend fun createNewPlayer(name: String, balance: Int): Player?
    suspend fun setBalanceTo(identity: UUID, amount: Int): Boolean
    suspend fun getPlayerByIdentifier(identity: UUID): Player?
    suspend fun randomPlayer(): Player
    suspend fun allPlayers(): List<Player>
}