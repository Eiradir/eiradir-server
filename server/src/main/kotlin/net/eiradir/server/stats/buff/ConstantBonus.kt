package net.eiradir.server.stats.buff

class ConstantBonus(private val constant: Int) : Buff {
    override fun apply(instance: BuffInstance, value: Int): Int {
        return value + constant
    }
}