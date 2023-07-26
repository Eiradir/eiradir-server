package net.eiradir.server.session

data class ResolvedLoginTokenSession(val loginToken: LoginTokenData) : Session, PlayerSession {
    override val username: String
        get() = loginToken.username

    override fun hasRole(role: String): Boolean {
        return loginToken.roles.contains(role)
    }
}