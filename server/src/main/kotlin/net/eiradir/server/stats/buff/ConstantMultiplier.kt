package net.eiradir.server.stats.buff

import kotlin.math.round

class ConstantMultiplier(private val constant: Float) : Buff {
    override fun apply(instance: BuffInstance, value: Int): Int {
        return round(value * constant).toInt()
    }
}