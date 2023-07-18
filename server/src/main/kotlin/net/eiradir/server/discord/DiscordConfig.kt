package net.eiradir.server.discord

data class DiscordConfigHolder(val discord: DiscordConfig)
data class DiscordConfig(
    val webhook: String?,
    val includeChat: Boolean = false,
    val includeSystem: Boolean = false,
    val token: String?
)