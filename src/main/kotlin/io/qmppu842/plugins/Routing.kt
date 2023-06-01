package io.qmppu842.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.qmppu842.database.playersDao

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/randPlayer"){
            call.respond(playersDao.randomPlayer())
        }

        get("/allPlayers"){
            call.respond(playersDao.allPlayers())
        }
    }
}
