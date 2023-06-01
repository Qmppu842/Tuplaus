package io.qmppu842.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.date.*
import io.qmppu842.GameEngine
import io.qmppu842.GameEvent
import io.qmppu842.GameEvents.bet
import io.qmppu842.GameEvents.comboId
import io.qmppu842.database.gameEventDao
import io.qmppu842.database.playersDao
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
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
            var userGameEvent: GameEvent? = null
            try {
                userGameEvent = call.receive<GameEvent>()

            } catch (excep: BadRequestException) {
                call.respond(HttpStatusCode.NotAcceptable)
            }
            if (userGameEvent != null) {

                val player = playersDao.getPlayerByIdentifier(UUID.fromString(userGameEvent.playerIdentity))
                if (player == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else if (player.balance < userGameEvent.bet) {
                    call.respond(HttpStatusCode.NotAcceptable)
                }

                val result = GameEngine.playGame()
                val winnings = if (result < 7 && !userGameEvent.isPlayerChoiceBig) {
                    userGameEvent.bet * 2
                } else if (result > 7 && userGameEvent.isPlayerChoiceBig) {
                    userGameEvent.bet * 2
                } else {
                    -userGameEvent.bet
                }

                val realEvent =
                    gameEventDao.newGame(
                        playerIdent = UUID.fromString(player!!.identity),
                        time = getTimeMillis(),
                        bet = userGameEvent.bet,
                        isPlayerChoiceBig = userGameEvent.isPlayerChoiceBig,
                        endCard = result,
                        winnings = winnings,
                        comboId = null
                    )

                playersDao.addToBalance(UUID.fromString(player.identity), winnings)
                if (realEvent != null) {
                    call.respond<GameEvent>(realEvent)
                } else {
                    call.respond(HttpStatusCode(418, "I am teapot"))
                }
            }
        }
    }
}
