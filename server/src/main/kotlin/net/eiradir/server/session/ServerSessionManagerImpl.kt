package net.eiradir.server.session

import arrow.core.Either
import net.eiradir.server.auth.Authentication
import net.eiradir.server.auth.Credentials
import net.eiradir.server.session.Session
import net.eiradir.server.session.LoginTokenSession
import java.time.Duration
import java.time.Instant
import java.util.*

class ServerSessionManagerImpl : ServerSessionManager {

    private val validDuration = Duration.ofMinutes(1)
    private val tokens = mutableMapOf<String, LoginTokenData>()

    private fun removeExpired() {
        tokens.values.removeIf { Instant.now().isAfter(it.validUntil) }
    }

    override suspend fun createSession(authentication: Authentication): Session {
        authentication as? JwtAccountAuthentication ?: throw IllegalArgumentException("Authentication must be of type JwtAccountAuthentication")

        removeExpired()

        val tokenData = LoginTokenData(UUID.randomUUID().toString(), authentication.accountId, authentication.username, authentication.roles, Instant.now().plus(validDuration))
        tokens[authentication.username] = tokenData
        return LoginTokenSession(authentication.username, tokenData.token)
    }

    override suspend fun verifySession(credentials: Credentials): Either<ServerSessionError, Session> {
        credentials as? LoginTokenCredentials ?: throw IllegalArgumentException("Credentials must be of type LoginTokenCredentials")

        removeExpired()

        val expectedToken = tokens[credentials.username]
        if (expectedToken != null && expectedToken.token == credentials.token) {
            tokens.remove(credentials.username)
            return Either.Right(ResolvedLoginTokenSession(expectedToken))
        } else {
            return Either.Left(ServerSessionError.SessionExpired)
        }
    }
}