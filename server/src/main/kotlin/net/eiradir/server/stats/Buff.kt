package net.eiradir.server.stats

interface Buff {
    fun apply(instance: BuffInstance, value: Int): Int
}