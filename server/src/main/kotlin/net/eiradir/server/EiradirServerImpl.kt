package net.eiradir.server

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.google.common.eventbus.EventBus
import net.eiradir.server.extensions.logger
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.PluginLoader
import net.eiradir.server.registry.Registries
import net.eiradir.server.config.ServerConfig
import net.eiradir.server.http.HttpServer
import net.eiradir.server.lifecycle.ServerSaveEvent
import net.eiradir.server.lifecycle.ServerStartedEvent
import net.eiradir.server.lifecycle.ServerStoppedEvent
import net.eiradir.server.network.NetworkServer
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ScheduledExecutorService
import kotlin.math.max

class EiradirServerImpl(private val koin: KoinApplication) : EiradirServer, KoinComponent {

    private val log = logger()
    private val pluginLoader by inject<PluginLoader>()
    private val networkServer by inject<NetworkServer>()
    private val httpServer by inject<HttpServer>()
    private val eventBus by inject<EventBus>()
    private val executor by inject<ScheduledExecutorService>()
    private val config by inject<ServerConfig>()
    private val engine by inject<Engine>()
    private val registries by inject<Registries>()
    private val mainThreadQueue by inject<MainThreadQueue>()

    override var isRunning = false

    fun start() {
        val plugins = pluginLoader.load().toList()
        plugins.forEach {
            log.info("Loaded plugin ${it.javaClass.simpleName}")
            koin.modules(it.provide())
            eventBus.register(it)
        }
        koin.createEagerInstances()
        getKoin().getAll<Initializer>()
        getKoin().getAll<EventBusSubscriber>().forEach {
            eventBus.register(it)
        }
        getKoin().getAll<EntitySystem>().forEach {
            engine.addSystem(it)
        }
        plugins.forEach {
            it.load(registries)
        }

        httpServer.start(config.httpPort)
        networkServer.start(config.port)

        isRunning = true

        eventBus.post(ServerStartedEvent(this))

        serverLoop()
    }

    override fun save() {
        eventBus.post(ServerSaveEvent(this))
    }

    override fun stop() {
        eventBus.post(ServerStoppedEvent(this))

        isRunning = false
        executor.shutdown()
        networkServer.stop()
        httpServer.stop()
    }

    private fun serverLoop() {
        var last = System.currentTimeMillis()
        var leftover = 0
        while (isRunning) {
            val now = System.currentTimeMillis()

            networkServer.processTasks()

            var delta = (now - last).toInt()

            if (delta > 2000) {
                val skipped = delta - 2000
                log.debug("Server is overloaded, skipping $skipped ms.")
                delta = 2000
            }

            leftover += delta
            last = now

            while (leftover > 50) {
                leftover -= 50
                tick(50)
            }

            vectorSanity()

            Thread.sleep(max(1, 50 - leftover).toLong())
        }
    }

    private fun vectorSanity() {
        if (!Vector3.Zero.isZero) {
            throw IllegalStateException("Vector3.Zero is not zero!")
        }

        if (!Vector2.Zero.isZero) {
            throw IllegalStateException("Vector2.Zero is not zero!")
        }
    }

    private fun tick(delta: Int) {
        engine.update(delta / 1000f)
        mainThreadQueue.processTasks()
    }

}