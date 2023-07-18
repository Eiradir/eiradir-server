package net.eiradir.server.sentry

import io.sentry.Sentry
import net.eiradir.server.plugin.Initializer

class SentryInitializer(config: SentryServerConfig) : Initializer {
    init {
        config.dsn?.let { dsn ->
            Sentry.init {
                it.dsn = dsn
            }
        }
    }
}