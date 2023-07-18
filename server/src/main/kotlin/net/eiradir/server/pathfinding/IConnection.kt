package net.eiradir.server.pathfinding

interface IConnection<N> {
    val cost: Float
    val fromNode: N
    val toNode: N
}
