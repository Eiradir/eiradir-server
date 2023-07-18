package net.eiradir.server.process.task

import ktx.ashley.mapperFor
import net.eiradir.server.EiradirServices
import net.eiradir.server.data.StatType
import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import net.eiradir.server.stats.buff.Buff
import net.eiradir.server.stats.buff.BuffInstance
import net.eiradir.server.stats.buff.StatBuffInstance
import net.eiradir.server.stats.buff.StatTagBuffInstance
import java.util.*


class BuffTask(private val stat: StatType, private val buff: Buff) : Task {
    override val taskId = UUID.randomUUID().toString()
    private val serviceMapper = mapperFor<EiradirServices>()
    override fun execute(context: ProcessContext): Boolean {
        val buffInstance = StatBuffInstance(buff, stat)
        serviceMapper[context.entity].stats.addBuff(context.entity, buffInstance)
        context.onSuccess {
            serviceMapper[context.entity].stats.removeBuff(context.entity, buffInstance)
        }
        context.onFailure {
            serviceMapper[context.entity].stats.removeBuff(context.entity, buffInstance)
        }
        return true
    }
}

class TagBuffTask(private val tag: String, private val buff: Buff) : Task {
    override val taskId = UUID.randomUUID().toString()
    private val serviceMapper = mapperFor<EiradirServices>()
    override fun execute(context: ProcessContext): Boolean {
        val buffInstance = StatTagBuffInstance(buff, tag)
        serviceMapper[context.entity].stats.addBuff(context.entity, buffInstance)
        context.onSuccess {
            serviceMapper[context.entity].stats.removeBuff(context.entity, buffInstance)
        }
        context.onFailure {
            serviceMapper[context.entity].stats.removeBuff(context.entity, buffInstance)
        }
        return true
    }
}

fun ProcessDefinitionBuilder.buff(stat: StatType, buff: Buff) {
    addTask(BuffTask(stat, buff))
}

fun ProcessDefinitionBuilder.buff(tag: String, buff: Buff) {
    addTask(TagBuffTask(tag, buff))
}