package net.eiradir.server.pathfinding.astar

interface IPathSmoother<TNode, TSmoothedNode> {
    fun smoothPathSync(path: NodePath<TNode>): NodePath<TSmoothedNode>
}