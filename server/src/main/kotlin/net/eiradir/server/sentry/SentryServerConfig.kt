package net.eiradir.server.sentry

data class SentryServerConfigHolder(val sentry: SentryServerConfig)

data class SentryServerConfig(
    val dsn: String?,
)