package net.eiradir.server.stats.buff

interface Buff {
    fun apply(instance: BuffInstance, value: Int): Int
}