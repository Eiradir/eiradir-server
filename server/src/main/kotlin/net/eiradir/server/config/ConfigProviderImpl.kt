package net.eiradir.server.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceOrFileSource

internal class ConfigProviderImpl(val args: Array<String>) : ConfigProvider {
    override fun getLoader(name: String): ConfigLoader {
        return ConfigLoaderBuilder.default()
            .addEnvironmentSource(useUnderscoresAsSeparator = true, allowUppercaseNames = true)
            .addResourceOrFileSource("$name.yaml")
            .build()
    }

    companion object {
        fun fromArgs(args: Array<String>): ConfigProvider {
            return ConfigProviderImpl(args)
        }
    }
}