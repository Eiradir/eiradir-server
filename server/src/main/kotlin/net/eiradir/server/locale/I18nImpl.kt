package net.eiradir.server.locale

import java.io.InputStream
import java.util.*

class I18nImpl : I18n, LocaleLoader {

    private val fallbackLocale = Locale.ENGLISH
    private val locale = Locale.ENGLISH
    private val messages = mutableMapOf<Locale, Properties>()

    override fun get(key: String): String {
        return messages[locale]?.getProperty(key) ?: messages[fallbackLocale]?.getProperty(key) ?: key
    }

    override fun load(locale: Locale, input: InputStream) {
        val properties = messages.getOrPut(locale) { Properties() }
        properties.load(input)
    }
}