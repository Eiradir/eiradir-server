package net.eiradir.content

import net.eiradir.server.hud.HudType
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries

class HudTypes(private val registries: Registries) : Initializer {

    val chat = registries.hudTypes.register(HudType("chat"))
    val vitality = registries.hudTypes.register(HudType("vitality"))
    val equipment = registries.hudTypes.register(HudType("equipment"))
    val traits = registries.hudTypes.register(HudType("traits"))
    val spells = registries.hudTypes.register(HudType("spells"))
    val cursor = registries.hudTypes.register(HudType("cursor"))
    val minimap = registries.hudTypes.register(HudType("minimap"))
}