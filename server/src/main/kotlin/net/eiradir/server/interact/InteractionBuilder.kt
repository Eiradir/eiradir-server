package net.eiradir.server.interact

import net.eiradir.server.io.SupportedByteBuf

class InteractionBuilder(
    val name: String,
) {
    private var handler: (InteractionContext) -> Unit = {}
    private var decoder: (SupportedByteBuf) -> InteractionParams? = { null }
    var isGlobal: Boolean = false

    fun handle(handler: (InteractionContext) -> Unit) {
        this.handler = handler
    }

    fun decode(decoder: (SupportedByteBuf) -> InteractionParams?) {
        this.decoder = decoder
    }

    fun build(): Interaction {
        return Interaction(name, handler).also {
            it.decoder = decoder
            it.isGlobal = isGlobal
        }
    }

}
