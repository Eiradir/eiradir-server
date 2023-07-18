package net.eiradir.server.extensions

import com.badlogic.ashley.core.Entity

fun Entity.copy(): Entity {
    val copy = Entity()
    this.components.asSequence().filter { it is CloneableComponent }.forEach {
        copy.add((it as CloneableComponent).copy())
    }
    return copy
}
