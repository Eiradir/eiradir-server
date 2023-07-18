package net.eiradir.server.extensions

fun String.ensureEndsWith(str: String): String {
    return if (this.endsWith(str)) this else this + str
}