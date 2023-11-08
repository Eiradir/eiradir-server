package net.eiradir.server.mobility

import com.badlogic.ashley.core.Component

class Mobility : Component {
    val moveQueue = mutableListOf<QueuedMove>()
}