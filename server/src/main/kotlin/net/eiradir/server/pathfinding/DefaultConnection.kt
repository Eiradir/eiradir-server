package net.eiradir.server.pathfinding

class DefaultConnection<TNode>(
    override val fromNode: TNode,
    override val toNode: TNode,
    override val cost: Float
) : IConnection<TNode>