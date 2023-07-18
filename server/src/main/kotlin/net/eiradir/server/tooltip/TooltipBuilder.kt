package net.eiradir.server.tooltip

class TooltipBuilder {
    val items = mutableListOf<TooltipItem>()

    companion object {
        fun build(init: TooltipBuilder.() -> Unit): Tooltip {
            return Tooltip(TooltipBuilder().apply(init).items)
        }
    }
}

fun TooltipBuilder.title(title: String) {
    items.add(TooltipTitle(title))
}