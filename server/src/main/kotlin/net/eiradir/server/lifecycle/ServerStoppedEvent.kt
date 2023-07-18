package net.eiradir.server.lifecycle

import net.eiradir.server.EiradirServer

data class ServerStoppedEvent(val server: EiradirServer)