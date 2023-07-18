package net.eiradir.server.session

import net.eiradir.server.session.Session
import net.eiradir.server.session.LoginTokenData
import net.eiradir.server.session.PlayerSession

data class ResolvedLoginTokenSession(val loginToken: LoginTokenData) : Session, PlayerSession {
    override val username: String
        get() = loginToken.username

    override fun hasRole(role: String): Boolean {
        return loginToken.roles.contains(role)
    }
}