package net.eiradir.server.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

fun Any.logger(): Logger {
    return LoggerFactory.getLogger(this.javaClass)
}

fun Any.marker(name: String): Marker {
    return MarkerFactory.getMarker(name)
}