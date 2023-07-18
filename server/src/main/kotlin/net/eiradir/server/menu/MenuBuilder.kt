package net.eiradir.server.menu

import net.eiradir.server.interact.Interaction

class MenuBuilder {
    val items = mutableListOf<MenuItem>()

    companion object {
        fun build(init: MenuBuilder.() -> Unit): Menu {
            return Menu(MenuBuilder().apply(init).items)
        }
    }
}

fun MenuBuilder.action(interaction: Interaction, label: String) {
    items.add(MenuAction(interaction, label))
}