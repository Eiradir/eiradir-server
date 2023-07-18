package net.eiradir.server.interact

interface Interactable {
    val interaction: Interaction
    fun handle(context: InteractionContext)
    fun isAvailable(context: InteractionContext): Boolean
    fun meetsRequirements(context: InteractionContext): Boolean
}