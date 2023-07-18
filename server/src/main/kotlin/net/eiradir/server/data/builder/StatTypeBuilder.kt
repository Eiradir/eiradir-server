package net.eiradir.server.data.builder

import com.badlogic.ashley.core.Entity
import net.eiradir.server.data.StatType

class StatTypeBuilder(
    private val name: String,
    private val default: (Entity) -> Int
) {

    private val tags = mutableSetOf<String>()

    fun tag(tag: String){
        tags.add(tag)
    }

    fun build(): StatType {
        return StatType(name, name, tags, default)
    }

}

