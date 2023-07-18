package net.eiradir.server.hud.entity

import com.badlogic.ashley.core.Component
import net.eiradir.server.hud.Hud

class HudComponent : Component {
    val huds: MutableMap<Int, Hud<*, *>> = mutableMapOf()
    var nextHudId: Int = 0
}