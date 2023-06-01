package io.qmppu842.database

import io.ktor.http.HttpHeaders.If
import io.qmppu842.Player
import io.qmppu842.Players
import io.qmppu842.Players.identity
import io.qmppu842.database.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import java.util.*

class PlayersDaoImpl : PlayersDao {
    private fun resultRowToPlayer(row: ResultRow) = Player(
        id = row[Players.id],
        name = row[Players.name],
        identity = row[identity].toString(),
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
        Players.update({ Players.identity eq identity }) {
            it[balance] = amount
        } > 0
    }

    override suspend fun addToBalance(identity: UUID, amount: Int): Boolean = dbQuery {
        Players.update({ Players.identity eq identity }) {
//            it[balance] = it[balance] + amount
            it.update(balance, balance + amount)
        } > 0
    }

    override suspend fun getPlayerByIdentifier(identity: UUID): Player? = dbQuery {
        Players.select {
            Players.identity eq identity
        }.map(::resultRowToPlayer).singleOrNull()
    }

    override suspend fun randomPlayer(): Player = dbQuery {
        Players.selectAll().map(::resultRowToPlayer).random()
    }

    override suspend fun allPlayers(): List<Player> = dbQuery {
        Players.selectAll().map(::resultRowToPlayer)
    }
}

val playersDao = PlayersDaoImpl().apply {
    runBlocking {
        if (allPlayers().isEmpty()) {
            createNewPlayer("Tester ", 10000)
        }
    }
}