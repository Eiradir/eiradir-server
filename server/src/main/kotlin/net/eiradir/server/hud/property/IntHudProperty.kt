package net.eiradir.server.hud.property

import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.HudProperty
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

class IntHudProperty<TProperty : Enum<TProperty>>(hud: Hud<TProperty, *>, key: TProperty, value: Int) : HudProperty<TProperty, Int>(hud, key, value) {
    override fun encode(buf: SupportedOutput) {
        buf.writeInt(value)
    }

    override fun decode(buf: SupportedInput): Int {
        return buf.readInt()
    }
}