package io.qmppu842.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import io.ktor.util.pipeline.*
import io.qmppu842.GameEngine
import io.qmppu842.GameEvent
import io.qmppu842.database.gameEventDao
import io.qmppu842.database.playersDao
import java.util.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/randPlayer") {
            call.respond(playersDao.randomPlayer())
        }

        get("/allPlayers") {
            call.respond(playersDao.allPlayers())
        }

        post("/playGame") {
            extracted()
        }

        post("/combo") {
            extracted()
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.extracted() {
    var userGameEvent: GameEvent? = null
    try {
        userGameEvent = call.receive<GameEvent>()

    } catch (excep: BadRequestException) {
        call.respond(HttpStatusCode.NotAcceptable)
    }
    if (userGameEvent != null) {

        var currentBet = userGameEvent.bet
        val player = playersDao.getPlayerByIdentifier(UUID.fromString(userGameEvent.playerIdentity))
        if (player == null) {
            call.respond(HttpStatusCode.NotFound)
        } else if (player.balance < currentBet) {
            call.respond(HttpStatusCode.NotAcceptable)
        }

        var comboId = 0
        if (currentBet == -1) {
            val lastGame = gameEventDao.getLastGameFromPlayer(UUID.fromString(player!!.identity))
            if (lastGame != null && lastGame.winnings > 0) {
                currentBet = lastGame.winnings
                comboId = lastGame.id
            } else {
                call.respond(HttpStatusCode.NotAcceptable)
            }
        }

        val result = GameEngine.playGame()
        val winnings = if (result < 7 && !userGameEvent.isPlayerChoiceBig) {
            currentBet * 2
        } else if (result > 7 && userGameEvent.isPlayerChoiceBig) {
            currentBet * 2
        } else {
            -currentBet
        }

        val realEvent =
            gameEventDao.newGame(
                playerIdent = UUID.fromString(player!!.identity),
                time = getTimeMillis(),
                bet = currentBet,
                isPlayerChoiceBig = userGameEvent.isPlayerChoiceBig,
                endCard = result,
                winnings = winnings,
                comboId = comboId
            )

        playersDao.addToBalance(UUID.fromString(player.identity), winnings)
        if (realEvent != null) {
            call.respond<GameEvent>(realEvent)
        } else {
            call.respond(HttpStatusCode(418, "I am teapot"))
        }
    }
}
