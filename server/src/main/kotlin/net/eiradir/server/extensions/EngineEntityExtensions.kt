package net.eiradir.server.extensions

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity

fun <T : Component> EngineEntity.add(component: T, configure: T.() -> Unit = {}) {
    entity.add(component).also { configure(component) }
}