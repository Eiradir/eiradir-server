package net.eiradir.server.map.entity

import com.badlogic.ashley.core.Component
import net.eiradir.server.entity.EntityBucket
import net.eiradir.server.entity.components.PersistedComponent
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum

class PersistenceComponent : Component, PersistedComponent {
    var bucket: EntityBucket = EntityBucket.Shared
    var isDirty = false

    override val serializedName: String
        get() = "Persistence"

    override fun save(buf: SupportedOutput) {
        buf.writeEnum(bucket)
    }

    override fun load(buf: SupportedInput) {
        bucket = buf.readEnum()
    }
}