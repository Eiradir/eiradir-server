package net.eiradir.content

import net.eiradir.server.locale.LocaleLoader
import net.eiradir.server.plugin.Initializer
import java.util.*

class Locales(private val localeLoader: LocaleLoader) : Initializer {
    init {
        val types = setOf("messages", "races", "items", "tiles", "interactions")
        val locales = setOf("en" to Locale.ENGLISH, "de" to Locale.GERMAN)
        types.forEach { type ->
            locales.forEach { (localeKey, locale) ->
                javaClass.getResourceAsStream("/messages/${type}_$localeKey.properties")?.let { localeLoader.load(locale, it) }
            }
        }
    }
}