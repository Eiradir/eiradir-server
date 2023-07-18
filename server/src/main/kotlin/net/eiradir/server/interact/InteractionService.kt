package net.eiradir.server.interact

class InteractionService(private val interactableRegistry: InteractableRegistry) {
    fun interact(context: InteractionContext) {
        val interactable = interactableRegistry.getInteractable(context) ?: return
        if (interactable.isAvailable(context) && interactable.meetsRequirements(context)) {
            interactable.handle(context)
        }
    }
}