package net.eiradir.server.interact

import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class Interaction(override val name: String, val handler: (InteractionContext) -> Unit = {}) : RegistryEntry<Interaction>, Interactable {

    var decoder: (SupportedByteBuf) -> InteractionParams? = { null }
    var isGlobal: Boolean = false

    override fun registry(registries: Registries): Registry<Interaction> {
        return registries.interactions
    }

    companion object {
        val Invalid = Interaction("invalid")
    }

    override val interaction: Interaction get() = this

    override fun handle(context: InteractionContext) {
        handler.invoke(context)
    }

    override fun isAvailable(context: InteractionContext): Boolean {
        return true
    }

    override fun meetsRequirements(context: InteractionContext): Boolean {
        return true
    }
}