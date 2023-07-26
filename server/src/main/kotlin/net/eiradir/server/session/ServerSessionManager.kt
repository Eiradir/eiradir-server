package net.eiradir.server.session

import arrow.core.Either
import net.eiradir.server.auth.EiradirAuthentication
import net.eiradir.server.auth.EiradirCredentials

interface ServerSessionManager {
    suspend fun createSession(authentication: EiradirAuthentication): Session
    suspend fun verifySession(credentials: EiradirCredentials): Either<ServerSessionError, Session>
}