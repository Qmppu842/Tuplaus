package io.qmppu842

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.qmppu842.database.DatabaseFactory
import io.qmppu842.plugins.configureRouting

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
    configureRouting()
}
