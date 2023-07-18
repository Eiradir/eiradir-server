package net.eiradir.server.tooltip

import net.eiradir.server.io.SupportedOutput

data class Tooltip(val items: List<TooltipItem>) {
    fun encode(buf: SupportedOutput) {
        buf.writeByte(items.size)
        items.forEach { it.encode(buf) }
    }
}