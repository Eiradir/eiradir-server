package net.eiradir.server.session

import net.eiradir.server.auth.EiradirCredentials

class LoginTokenCredentials(val username: String, val token: String) : EiradirCredentials