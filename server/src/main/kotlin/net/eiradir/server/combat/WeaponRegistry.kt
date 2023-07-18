package net.eiradir.server.combat

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class WeaponRegistry(idResolver: IdResolver) : Registry<Weapon>("weapons", idResolver) {
    override fun invalid(name: String): Weapon {
        return Weapon(name, WeaponType.UNARMED, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null)
    }
}