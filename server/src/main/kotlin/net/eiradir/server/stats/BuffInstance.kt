package net.eiradir.server.stats

import net.eiradir.server.data.StatType

sealed class BuffInstance(val buff: Buff) {
    var enabled: Boolean = true
    var secondsPassed: Float = 0f

    fun applyBuff(value: Int): Int {
        if (!enabled) {
            return value
        }
        return buff.apply(this, value)
    }
}

class StatBuffInstance(buff: Buff, val statType: StatType) : BuffInstance(buff)
class StatTagBuffInstance(buff: Buff, val statTag: String) : BuffInstance(buff)