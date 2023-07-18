package net.eiradir.server.combat

enum class DualWielding {
    Disallow,
    Allow,
    RequireSymmetry;

    companion object {
        private val values = DualWielding.values()

        fun fromId(id: Int): DualWielding {
            return values[id]
        }
    }
}
