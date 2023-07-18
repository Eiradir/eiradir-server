package net.eiradir.server.interact

class InteractableEntry(
    override val interaction: Interaction,
    val handler: (InteractionContext) -> Unit,
    val condition: (InteractionContext) -> Boolean,
    val requirement: (InteractionContext) -> Boolean
) : Interactable {
    override fun handle(context: InteractionContext) {
        handler.invoke(context)
    }

    override fun isAvailable(context: InteractionContext): Boolean {
        return condition.invoke(context)
    }

    override fun meetsRequirements(context: InteractionContext): Boolean {
        return requirement.invoke(context)
    }
}