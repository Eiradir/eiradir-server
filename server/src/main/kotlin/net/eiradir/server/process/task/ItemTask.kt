package net.eiradir.server.process.task

import net.eiradir.server.item.ItemInstance
import net.eiradir.server.process.ProcessContext
import net.eiradir.server.process.ProcessDefinitionBuilder
import net.eiradir.server.process.Task
import net.eiradir.server.services
import java.util.*


class ItemTask(private val name: String, private val count: Int = 1) : Task {
    override val taskId = UUID.randomUUID().toString()
    override fun execute(context: ProcessContext): Boolean {
        val services = context.entity.services()
        val item = services.registries.items.getByName(name) ?: return true
        services.inventory.giveOrDropItem(context.entity, ItemInstance(item, count))
        println("you got $count $name")
        return true
    }
}

fun ProcessDefinitionBuilder.item(name: String, count: Int = 1) {
    addTask(ItemTask(name, count))
}
