package net.eiradir.server.item

import com.badlogic.gdx.graphics.Color
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.eiradir.server.data.Item

// TODO we should really move the json stuff out of the gameplay class considering it's not even the real final persistence method
class ItemInstance(val item: Item, count: Int = 1) {
    @get:JsonIgnore
    var owner: ItemOwner<Any>? = null; private set
    private var context: Any? = null
    var count: Int = count; private set
    @field:JsonProperty("data")
    private val _data = mutableMapOf<String, String>()
    @get:JsonIgnore
    val data: Map<String, String> get() = _data

    @field:JsonIgnore
    private var cachedColor: Color? = null
    @get:JsonIgnore
    val color: Color
        get() {
            val cachedColor = cachedColor ?: return (_data["color"]?.let { Color.valueOf(it) } ?: Color.WHITE).also {
                this.cachedColor = it
            }
            return cachedColor
        }

    fun setData(key: String, value: String) {
        _data[key] = value
        if (key == ItemDataKeys.COLOR) {
            cachedColor = null
        }
        owner?.itemInstanceChanged(context!!, this)
    }

    fun <T : Any> setOwner(owner: ItemOwner<T>, context: T) {
        @Suppress("UNCHECKED_CAST")
        this.owner = owner as ItemOwner<Any>
        this.context = context
    }

    fun substack(count: Int): ItemInstance {
        val cpy = copy()
        cpy.count = count.coerceAtMost(this.count)
        this.count -= cpy.count
        owner?.itemInstanceChanged(context!!, this)
        return cpy
    }

    fun copy(): ItemInstance {
        return ItemInstance(item, count).also {
            it.owner = owner
            it.context = context
            it._data.putAll(_data)
        }
    }

    fun replaceWith(newItem: ItemInstance) {
        owner?.replaceItemInstance(context!!, newItem)
    }

    fun grow(amount: Int) {
        count += amount
        owner?.itemInstanceChanged(context!!, this)
    }

    fun shrink(amount: Int) {
        count -= amount
        owner?.itemInstanceChanged(context!!, this)
    }

    @get:JsonIgnore
    val isEmpty get() = count == 0
    @get:JsonIgnore
    val isNotEmpty get() = !isEmpty

    companion object {
        val Empty = ItemInstance(Item.Invalid, 0)
    }
}