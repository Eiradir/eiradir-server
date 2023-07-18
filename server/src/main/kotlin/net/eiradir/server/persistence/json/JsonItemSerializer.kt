package net.eiradir.server.persistence.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import net.eiradir.server.data.Item

class JsonItemSerializer : StdSerializer<Item>(Item::class.java) {
    override fun serialize(value: Item, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.name)
    }
}