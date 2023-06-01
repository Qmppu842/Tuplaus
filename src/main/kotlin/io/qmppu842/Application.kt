package io.qmppu842

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.qmppu842.database.DatabaseFactory
import io.qmppu842.plugins.configureRouting

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
    configureRouting()

    install(ContentNegotiation) {
        json()
    }
}
