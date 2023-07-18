package net.eiradir.server.session

import java.time.Instant

data class LoginTokenData(val token: String, val accountId: String, val username: String, val roles: Set<String>, val validUntil: Instant)