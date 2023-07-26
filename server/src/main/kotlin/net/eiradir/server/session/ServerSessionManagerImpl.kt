package net.eiradir.server.session

import arrow.core.Either
import net.eiradir.server.auth.EiradirAuthentication
import net.eiradir.server.auth.EiradirCredentials
import net.eiradir.server.auth.JwtAccountAuthentication
import java.time.Duration
import java.time.Instant
import java.util.*

class ServerSessionManagerImpl : ServerSessionManager {

    private val validDuration = Duration.ofMinutes(1)
    private val tokens = mutableMapOf<String, LoginTokenData>()

    private fun removeExpired() {
        tokens.values.removeIf { Instant.now().isAfter(it.validUntil) }
    }

    override suspend fun createSession(authentication: EiradirAuthentication): Session {
        authentication as? JwtAccountAuthentication ?: throw IllegalArgumentException("Authentication must be of type JwtAccountAuthentication")

        removeExpired()

        val tokenData = LoginTokenData(UUID.randomUUID().toString(), authentication.accountId, authentication.username, authentication.roles, Instant.now().plus(validDuration))
        tokens[authentication.username] = tokenData
        return LoginTokenSession(authentication.username, tokenData.token)
    }

    override suspend fun verifySession(credentials: EiradirCredentials): Either<ServerSessionError, Session> {
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