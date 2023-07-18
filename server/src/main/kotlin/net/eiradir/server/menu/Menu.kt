package net.eiradir.server.menu

import net.eiradir.server.io.SupportedOutput

data class Menu(val items: List<MenuItem>) {
    fun encode(buf: SupportedOutput) {
        buf.writeByte(items.size)
        items.forEach { it.encode(buf) }
    }
}