package net.eiradir.server.interact

class InteractableBuilder(private val interaction: Interaction) {

    private var handler: (InteractionContext) -> Unit = { interaction.handler(it) }
    private var condition: (InteractionContext) -> Boolean = { true }
    private var requirement: (InteractionContext) -> Boolean = { true }

    fun handle(function: (InteractionContext) -> Unit) {
        this.handler = function
    }

    /**
     * The interaction will only be available to this context if the given predicate matches. Unavailable interactions will not be shown in the menu.
     */
    fun conditionally(function: (InteractionContext) -> Boolean) {
        this.condition = function
    }

    /**
     * The interaction will only be enabled on this context if the given predicate matches. Disabled interactions will still show in the menu, but cannot be selected.
     */
    fun requires(function: (InteractionContext) -> Boolean) {
        this.requirement = function
    }

    fun build(): InteractableEntry {
        return InteractableEntry(interaction, handler, condition, requirement)
    }
}