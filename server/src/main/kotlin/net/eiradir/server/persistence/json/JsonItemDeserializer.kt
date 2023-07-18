package net.eiradir.server.persistence.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.eiradir.server.data.Item
import net.eiradir.server.registry.Registries

class JsonItemDeserializer(private val registries: Registries) : StdDeserializer<Item>(Item::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Item {
        val itemName = p.valueAsString
        return registries.items.getByName(itemName) ?: Item.Invalid
    }
}