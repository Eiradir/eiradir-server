package net.eiradir.server.persistence

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.persistence.json.JsonInventoryStorage
import net.eiradir.server.persistence.json.JsonCharacterStorage
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DatabasePersistencePlugin : EiradirPlugin {

    override fun provide() = module {
        singleOf(::DatabasePersistenceSystem) bind EntitySystem::class
        singleOf(::JsonCharacterStorage) bind CharacterStorage::class
        singleOf(::JsonInventoryStorage) bind InventoryStorage::class
    }
}