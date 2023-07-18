package net.eiradir.server.data

import net.eiradir.server.interact.InteractableEntry
import net.eiradir.server.interact.Interaction
import net.eiradir.server.item.EquipmentSlot
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class Item(
    override val name: String,
    val isoName: String = name,
    val maxStackSize: Int = 1,
    val worth: Int = 0,
    val equipmentSlot: EquipmentSlot = EquipmentSlot.None,
    val twoHanded: Boolean = false,
    val tooltip: String? = null,
    val gameSystem: String? = null,
    val restItem: ItemReference? = null,
    val tags: Set<String> = emptySet()
) : RegistryEntry<Item>, IsoType {
    val interactions = mutableMapOf<Interaction, InteractableEntry>()

    override fun registry(registries: Registries): Registry<Item> {
        return registries.items
    }

    override fun isoId(registries: Registries): Int {
        return registries.idResolver.resolve("isos", isoName) ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        val Invalid = Item("invalid", "invalid", 0, 0, EquipmentSlot.None, false, null, null, null, emptySet())
    }
}