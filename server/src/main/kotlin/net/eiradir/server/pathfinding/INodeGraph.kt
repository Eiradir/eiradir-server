package net.eiradir.server.pathfinding

import com.badlogic.gdx.math.Vector3

interface INodeGraph<TKey, TNode> : IConnectionGraph<TKey, TNode> {
    fun getNodeKey(node: TNode): TKey
    fun getNodeAt(position: Vector3, searchMode: NodeSearchMode = NodeSearchMode.Downwards): TNode?
    fun isValidEndNode(node: TNode): Boolean
}