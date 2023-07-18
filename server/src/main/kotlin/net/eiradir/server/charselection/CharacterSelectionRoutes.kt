package net.eiradir.server.charselection

import com.google.common.eventbus.Subscribe
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.auth.KeycloakPrincipal
import net.eiradir.server.lifecycle.KtorSetupEvent
import net.eiradir.server.persistence.CharacterStorage

class CharacterSelectionRoutes(private val characterStorage: CharacterStorage) : EventBusSubscriber {

    @Subscribe
    fun onKtorSetup(event: KtorSetupEvent) {
        event.configure {
            routing {
                authenticate {
                    get("/characters") {
                        val principal = call.principal<KeycloakPrincipal>()!!
                        val characters = characterStorage.getCharactersByAccountId(principal.subject!!)
                        call.respond(CharacterListResponse(characters.map { CharacterListResponsePlayer(it.id, it.name) }))
                    }
                }
            }
        }
    }

}