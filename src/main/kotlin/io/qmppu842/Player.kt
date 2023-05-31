package io.qmppu842

import org.jetbrains.exposed.sql.Table
import java.util.*

/**
 * @param balance is counted to cent accuracy
 */
data class Player(val id: Int, val identity: UUID, val name: String, var balance: Int)

object Players : Table() {
    val id = integer("id").autoIncrement()
    val identity = uuid("identity").autoGenerate()
    val name = varchar("name", 1024)
    var balance = integer("balance")

    override val primaryKey = PrimaryKey(id)

}
