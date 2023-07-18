package net.eiradir.server.pathfinding.astar

interface IPathFinder<TNode> {

    fun searchNodePathSync(startNode: TNode, endNode: TNode, heuristic: IHeuristic<TNode>): NodePath<TNode>?
}
