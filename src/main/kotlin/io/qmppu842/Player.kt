package io.qmppu842

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.util.*

/**
 * @param balance is counted to cent accuracy
 */
@Serializable
data class Player(val id: Int, val identity: String, val name: String, var balance: Int)

object Players : Table() {
    val id = integer("id").autoIncrement()
    val identity = uuid("identity").autoGenerate()
    val name = varchar("name", 1024)
    var balance = integer("balance")

    override val primaryKey = PrimaryKey(id)

}
