package net.eiradir.server.pathfinding.astar

class PathfinderMetrics {
    var visitedNodes: Int = 0
    var openListAdditions: Int = 0
    var openListPeak: Int = 0

    fun reset() {
        visitedNodes = 0
        openListAdditions = 0
        openListPeak = 0
    }
}