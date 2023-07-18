package net.eiradir.server.locale

import java.io.InputStream
import java.util.Locale

interface LocaleLoader {
    fun load(locale: Locale, input: InputStream)
}