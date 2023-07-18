package net.eiradir.server.data

import net.eiradir.server.registry.Registries

interface ItemReference {
    val isValid: Boolean
    fun resolve(registries: Registries): Item
}

class ItemReferenceImpl(val name: String) : ItemReference {
    private var item: Item? = null

    override val isValid: Boolean get() = item != null

    override fun resolve(registries: Registries): Item {
        // get item from items registry and cache it
        return item ?: registries.items.getByName(name)?.also {
            item = it
        } ?: Item.Invalid
    }
}

//class ItemTagReferenceImpl(val tag: String) : ItemReference {
//    private val items by lazy { ItemRegistry.getItemsByTag(tag) }
//
//    override val isValid: Boolean get() = items.isNotEmpty()
//
//    override fun resolve(registries: Registries): Item {
//        return items.firstOrNull() ?: Item.Invalid
//    }
//}

fun itemRef(name: String): ItemReference {
    return ItemReferenceImpl(name)
}

//fun tagRef(tag: String): ItemReference {
//    return ItemTagReferenceImpl(tag)
//}