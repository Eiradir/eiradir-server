package net.eiradir.server.hud.property

import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.HudProperty
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

class StringHudProperty<TProperty : Enum<TProperty>>(hud: Hud<TProperty, *>, key: TProperty, value: String) : HudProperty<TProperty, String>(hud, key, value) {
    override fun encode(buf: SupportedOutput) {
        buf.writeString(value)
    }

    override fun decode(buf: SupportedInput): String {
        return buf.readString()
    }
}