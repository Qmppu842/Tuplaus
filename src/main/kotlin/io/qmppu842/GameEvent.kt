package io.qmppu842

import org.jetbrains.exposed.sql.Table
import java.util.*

data class GameEvent(
    val id: Int,
    val playerIdentity: UUID,
    val timestamp: Long = System.currentTimeMillis(),
    val bet: Int,
    val endCard: Int,
    val winnings: Int = 0
)

object GameEvents : Table() {
    val id = integer("id").autoIncrement()
    val playerIdentity = reference("playerIdentity", Players.identity)
    val timestamp = long("timestamp")
    var bet = integer("bet")
    val endCard = integer("endCard")
    val winnings = integer("winnings")

    override val primaryKey = PrimaryKey(id)

}
