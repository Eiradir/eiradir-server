package net.eiradir.server.auth

import arrow.core.Either

interface AuthenticationService {
    suspend fun authenticate(credentials: Credentials): Either<AuthenticationError, Authentication>
    suspend fun refreshAuthentication(authentication: Authentication): Either<AuthenticationError, Authentication>
}