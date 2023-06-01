package io.qmppu842

import io.ktor.util.date.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class GameEvent(
    val id: Int = -1,
    val playerIdentity: String,
    val timestamp: Long = getTimeMillis(),
    val bet: Int = -1,
    val isPlayerChoiceBig: Boolean,
    val endCard: Int = -1,
    val winnings: Int = 0,
    val comboId: Int = -1
)

object GameEvents : Table() {
    val id = integer("id").autoIncrement()
    val playerIdentity = reference("playerIdentity", Players.identity)
    val timestamp = long("timestamp")
    var bet = integer("bet")
    val isPlayerChoiceBig = bool("isPlayerChoiceBig")
    val endCard = integer("endCard")
    val winnings = integer("winnings")
    val comboId = reference("combo", id)

    override val primaryKey = PrimaryKey(id)

}
