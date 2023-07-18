package net.eiradir.server

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.SLF4JLogger
import java.util.concurrent.Executors


fun main(args: Array<String>) {
    val serverModule = createServerModule(args)
    val serverDistModule = module {
        single { Executors.newScheduledThreadPool(1) }
        single {
            HttpClient {
                install(ContentNegotiation) {
                    jackson()
                }
            }
        }
    }

    val koin = startKoin {
        logger(SLF4JLogger())

        modules(serverModule, serverDistModule)
    }

    EiradirServerImpl(koin).start()
}