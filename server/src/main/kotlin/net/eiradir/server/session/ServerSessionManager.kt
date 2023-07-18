package net.eiradir.server.session

import arrow.core.Either
import net.eiradir.server.auth.Authentication
import net.eiradir.server.auth.Credentials
import net.eiradir.server.session.Session

interface ServerSessionManager {
    suspend fun createSession(authentication: Authentication): Session
    suspend fun verifySession(credentials: Credentials): Either<ServerSessionError, Session>
}