package net.eiradir.server

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.SubscriberExceptionHandler
import net.eiradir.server.charcreation.PlayableRaceRegistry
import net.eiradir.server.combat.ArmorRegistry
import net.eiradir.server.combat.WeaponRegistry
import net.eiradir.server.config.ConfigProvider
import net.eiradir.server.config.ConfigProviderImpl
import net.eiradir.server.entity.EngineQueue
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.PluginLoader
import net.eiradir.server.plugin.SpiPluginLoader
import net.eiradir.server.config.ServerConfigHolder
import net.eiradir.server.data.*
import net.eiradir.server.event.EiradirSubscriberExceptionHandler
import net.eiradir.server.event.LoggingEventBus
import net.eiradir.server.exception.ExceptionHandler
import net.eiradir.server.exception.LoggingExceptionHandler
import net.eiradir.server.http.HttpClientFactory
import net.eiradir.server.http.HttpClientFactoryImpl
import net.eiradir.server.http.HttpServer
import net.eiradir.server.http.HttpServerImpl
import net.eiradir.server.nature.NatureGenerator
import net.eiradir.server.network.NetworkServer
import net.eiradir.server.network.NetworkServerImpl
import net.eiradir.server.plugin.EiradirServerPlugin
import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.RegistryBuilders
import net.eiradir.server.registry.StaticIdMappingsResolver
import net.eiradir.server.script.ScriptLoader
import net.eiradir.server.status.ServerStatusProvider
import net.eiradir.server.status.ServerStatusProviderImpl
import net.eiradir.server.trait.TraitRegistry
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun createServerModule(args: Array<String>): Module = module {
    singleOf(::ServerStatusProviderImpl) { bind<ServerStatusProvider>() }
    singleOf(::PacketFactory)
    singleOf(::NetworkServerImpl) bind NetworkServer::class
    singleOf(::HttpServerImpl) bind HttpServer::class
    single { get<ConfigProvider>(ConfigProvider::class).getLoader("server").loadConfigOrThrow<ServerConfigHolder>().server }
    single { createPluginLoader() }
    single { EiradirServices(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single {
        object : Engine() {
            override fun createEntity(): Entity {
                val entity = super.createEntity()
                entity.add(get<EiradirServices>())
                return entity
            }
        }
    } bind Engine::class
    singleOf(::EngineQueue) bind EntitySystem::class
    singleOf(::NatureGenerator)
    single { ConfigProviderImpl.fromArgs(args) }
    singleOf(::HttpClientFactoryImpl) { bind<HttpClientFactory>() }
    singleOf(::LoggingExceptionHandler) { bind<ExceptionHandler>() }
    singleOf(::EiradirSubscriberExceptionHandler) { bind<SubscriberExceptionHandler>() }
    singleOf(::LoggingEventBus) { bind<EventBus>() }
    singleOf(::ScriptLoader)
    singleOf(::MainThreadQueue)
    single { createIdResolver() }
    singleOf(::TileRegistry)
    singleOf(::ItemRegistry)
    singleOf(::FoodRegistry)
    singleOf(::WeaponRegistry)
    singleOf(::ArmorRegistry)
    singleOf(::ComponentRegistry)
    singleOf(::StatTypeRegistry)
    singleOf(::RaceRegistry)
    singleOf(::PlayableRaceRegistry)
    single { Registries(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    singleOf(::RegistryBuilders)
}

private fun createPluginLoader(): PluginLoader {
    return SpiPluginLoader(EiradirServerPlugin::class.java)
}

fun createIdResolver(): IdResolver {
    return StaticIdMappingsResolver().apply {
        IdResolver::class.java.getResourceAsStream("/id_mappings.ini")?.reader()?.use {
            load(it)
        } ?: throw IllegalStateException("Unable to load id_mappings.ini")
    }
}