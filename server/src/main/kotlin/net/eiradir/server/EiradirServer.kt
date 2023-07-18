package net.eiradir.server

interface EiradirServer {
    val isRunning: Boolean
    fun save()
    fun stop()
}