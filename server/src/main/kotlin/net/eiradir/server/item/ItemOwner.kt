package net.eiradir.server.item

interface ItemOwner<T> {
    fun itemInstanceChanged(context: T, itemInstance: ItemInstance)
    fun replaceItemInstance(context: T, itemInstance: ItemInstance)
}