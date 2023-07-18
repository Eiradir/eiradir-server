package net.eiradir.server.pathfinding

import java.util.stream.Stream

interface IConnectionGraph<TKey, TNode> {
    fun getConnections(graph: INodeGraph<TKey, TNode>, fromNode: TNode): Stream<IConnection<TNode>>
}