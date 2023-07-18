package net.eiradir.server.hud.property

import net.eiradir.server.hud.HudType
import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class HudTypeRegistry(idResolver: IdResolver): Registry<HudType>("hud_types", idResolver) {
    override fun invalid(name: String): HudType {
        return HudType(name)
    }
}