package net.eiradir.server.tooltip

import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.writeEnum

enum class TooltipItemType {
    Title
}

sealed interface TooltipItem {
    fun encode(buf: SupportedOutput)
}

data class TooltipTitle(val title: String) : TooltipItem {
    override fun encode(buf: SupportedOutput) {
        buf.writeEnum(TooltipItemType.Title)
        buf.writeString(title)
    }
}