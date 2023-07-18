package net.eiradir.server.http

interface HttpServer {
    fun start(port: Int)
    fun stop()
}