package net.eiradir.server.locale

import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

class I18nPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::I18nImpl) binds arrayOf(I18n::class, LocaleLoader::class)
    }
}