package net.eiradir.server.entity

sealed class EntityCreationError {
    object InvalidRace : EntityCreationError()
    object InvalidIsoType : EntityCreationError()
    object InvalidInventory : EntityCreationError()
}