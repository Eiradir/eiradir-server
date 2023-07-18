package net.eiradir.server.lifecycle

import net.eiradir.server.EiradirServer

data class ServerStartedEvent(val server: EiradirServer)