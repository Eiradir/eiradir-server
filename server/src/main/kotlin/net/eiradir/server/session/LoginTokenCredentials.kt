package net.eiradir.server.session

import net.eiradir.server.auth.Credentials

class LoginTokenCredentials(val username: String, val token: String) : Credentials