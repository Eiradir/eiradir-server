package net.eiradir.server.interact

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.HashBasedTable

class InteractableRegistry {

    /** TODO maybe pointless idk */
    private val globalInteractables = ArrayListMultimap.create<Interaction, InteractableEntry>()

    private val tagInteractables = HashBasedTable.create<String, Interaction, InteractableEntry>()

    fun registerGlobalInteractable(interaction: Interaction, interactable: InteractableEntry) {
        globalInteractables.put(interaction, interactable)
    }

    fun registerTagInteractable(tag: String, interaction: Interaction, interactable: InteractableEntry) {
        tagInteractables.put(tag, interaction, interactable)
    }

    fun getInteractables(context: InteractionContext): List<Interactable> {
        val result = globalInteractables.values().toMutableList()
        if (context.item.isNotEmpty) {
            val item = context.item.item
            for ((_, interactable) in item.interactions) {
                result.add(interactable)
            }
            for (tag in item.tags) {
                for ((_, interactable) in tagInteractables.row(tag)) {
                    result.add(interactable)
                }
            }
        }
        return result
    }

    fun getInteractable(context: InteractionContext): Interactable? {
        val interaction = context.interaction
        if (context.item.isNotEmpty) {
            val item = context.item.item
            val itemInteractable = item.interactions[interaction]
            if (itemInteractable != null) {
                return itemInteractable
            }
            for (tag in item.tags) {
                val tagInteractable = tagInteractables.get(tag, interaction)
                if (tagInteractable != null) {
                    return tagInteractable
                }
            }
        }

        if (globalInteractables.containsKey(interaction)) {
            return globalInteractables.get(interaction).first {
                it.isAvailable(context) && it.meetsRequirements(context)
            }
        }

        return if (interaction.isGlobal) interaction else null
    }
}