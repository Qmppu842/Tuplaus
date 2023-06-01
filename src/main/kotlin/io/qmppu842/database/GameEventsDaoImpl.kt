package io.qmppu842.database

import io.qmppu842.GameEvent
import io.qmppu842.GameEvents
import io.qmppu842.database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

class GameEventsDaoImpl : GameEventsDao {
    private fun resultRowToGameEvent(row: ResultRow) = GameEvent(
        id = row[GameEvents.id],
        playerIdentity = row[GameEvents.playerIdentity].toString(),
        timestamp = row[GameEvents.timestamp],
        bet = row[GameEvents.bet],
        isPlayerChoiceBig = row[GameEvents.isPlayerChoiceBig],
        endCard = row[GameEvents.endCard],
        winnings = row[GameEvents.winnings],
        comboId = row[GameEvents.comboId]
    )

    override suspend fun newGame(
        playerIdent: UUID,
        time: Long,
        bet: Int,
        isPlayerChoiceBig: Boolean,
        endCard: Int,
        winnings: Int,
        comboId: Int?
    ): GameEvent? = dbQuery {
        val insert = GameEvents.insert {
            it[playerIdentity] = playerIdent
            it[timestamp] = time
            it[GameEvents.bet] = bet
            it[GameEvents.isPlayerChoiceBig] = isPlayerChoiceBig
            it[GameEvents.endCard] = endCard
            it[GameEvents.winnings] = winnings
            it[GameEvents.comboId] = comboId
        }
        insert.resultedValues?.single()?.let(::resultRowToGameEvent)
    }

    override suspend fun getLastGameFromPlayer(playerIdentity: UUID): GameEvent? = dbQuery {
        GameEvents.select {
            GameEvents.playerIdentity eq playerIdentity
        }.orderBy(GameEvents.timestamp, SortOrder.DESC).map(::resultRowToGameEvent).firstOrNull()
    }
}

val gameEventDao = GameEventsDaoImpl()