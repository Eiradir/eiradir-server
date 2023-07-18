package net.eiradir.server.pathfinding.astar

interface IHeuristic<in TNode> {

    fun estimate(node: TNode, endNode: TNode): Float
}
