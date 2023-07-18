package net.eiradir.server.hud.property

import net.eiradir.server.data.IconType
import net.eiradir.server.data.StatType
import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.HudProperty
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.registry.Registries
import java.util.*

class IconArrayHudProperty<TProperty : Enum<TProperty>>(hud: Hud<TProperty, *>, key: TProperty, value: Array<IconType>) :
    HudProperty<TProperty, Array<IconType>>(hud, key, value) {
    override fun encode(buf: SupportedOutput) {
        buf.writeShort(value.size)
        for (icon in value) {
            buf.writeShort(icon.iconId(buf.registries))
            buf.writeString(icon.name)
        }
    }

    override fun decode(buf: SupportedInput): Array<IconType> {
        throw UnsupportedOperationException()
    }

    override fun hasChanged(value: Array<IconType>, lastValue: Array<IconType>): Boolean {
        return !value.contentEquals(lastValue)
    }
}