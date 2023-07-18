package net.eiradir.server.config

import com.sksamuel.hoplite.ConfigLoader


interface ConfigProvider {
    fun getLoader(name: String): ConfigLoader
}