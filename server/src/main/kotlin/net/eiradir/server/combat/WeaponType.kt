package net.eiradir.server.combat

/**
 * Note that the ordinals of this enum are mapped in the database. Removals should leave a placeholder behind,
 * additions should only be added to the end of the list, to prevent all ordinals shifting.
 */
enum class WeaponType {
    SMALL_BLADE,
    SWORD,
    AXE,
    POLE_ARM,
    STAVE,
    HAMMER,
    BOW,
    CROSSBOW,
    SHIELD,
    THROWING_WEAPON,
    UNARMED,
    MACE;

    val isWeapon: Boolean
        get() {
            return this != SHIELD
        }

    companion object {
        private val values = values()

        fun fromId(id: Int): WeaponType {
            return values[id]
        }
    }
}
