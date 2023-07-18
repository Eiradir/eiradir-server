package net.eiradir.server.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem

class EngineQueue(private val engine: Engine) : EntitySystem() {

    enum class Operation {
        ADD, REMOVE
    }

    private val operationQueue = mutableMapOf<Entity, Operation>()

    override fun update(deltaTime: Float) {
        val maxOperations = 100
        var operations = 0
        synchronized(operationQueue) {
            val iterator = operationQueue.iterator()
            while (iterator.hasNext() && operations < maxOperations) {
                val operation = iterator.next()
                when (operation.value) {
                    Operation.ADD -> {
                        engine.addEntity(operation.key)
                    }
                    Operation.REMOVE -> {
                        engine.removeEntity(operation.key)
                    }
                }
                iterator.remove()
                operations++
            }
        }
    }

    fun addEntity(entity: Entity) {
        engine.addEntity(entity)
        /* TODO synchronized(operationQueue) {
            if (operationQueue[entity] == Operation.REMOVE) {
                operationQueue.remove(entity)
            } else {
                operationQueue[entity] = Operation.ADD
            }
        }*/
    }

    fun removeEntity(entity: Entity) {
        synchronized(operationQueue) {
            if (operationQueue[entity] == Operation.ADD) {
                operationQueue.remove(entity)
            } else {
                operationQueue[entity] = Operation.REMOVE
            }
        }
    }

}