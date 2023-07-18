package net.eiradir.server.session

import net.eiradir.server.session.Session

data class LoginTokenSession(val username: String, val token: String) : Session