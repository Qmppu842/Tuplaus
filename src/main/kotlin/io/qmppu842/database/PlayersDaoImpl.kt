package io.qmppu842.database

import io.qmppu842.Player
import io.qmppu842.Players
import io.qmppu842.database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

class PlayersDaoImpl : PlayersDao {
    private fun resultRowToPlayer(row: ResultRow) = Player(
        id = row[Players.id],
        name = row[Players.name],
        identity = row[Players.identity],
        balance = row[Players.balance]
    )

    override suspend fun createNewPlayer(name: String, balance: Int): Player? = dbQuery {
        val insert = Players.insert {
            it[Players.name] = name
            it[Players.balance] = balance
        }
        insert.resultedValues?.singleOrNull()?.let(::resultRowToPlayer)
    }

    override suspend fun setBalanceTo(identity: UUID, amount: Int): Boolean = dbQuery {
        Players.update ({Players.identity eq identity}){
            it[balance] = amount
        } > 0
    }

    override suspend fun getPlayerByIdentifier(identity: UUID): Player? = dbQuery {
        Players.select {
            Players.identity eq identity
        }.map(::resultRowToPlayer).singleOrNull()
    }
}