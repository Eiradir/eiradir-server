package net.eiradir.server.menu

import net.eiradir.server.interact.Interaction
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.writeEnum

enum class MenuItemType {
    Action
}

sealed interface MenuItem {
    fun encode(buf: SupportedOutput)
}

data class MenuAction(val interaction: Interaction, val label: String) : MenuItem {
    override fun encode(buf: SupportedOutput) {
        buf.writeEnum(MenuItemType.Action)
        buf.writeId(interaction)
        buf.writeString(label)
    }
}