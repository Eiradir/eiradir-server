package net.eiradir.server.commands

import com.badlogic.ashley.core.Entity
import net.eiradir.server.commands.CommandSource

interface EntityCommandSource : CommandSource {
    val entity: Entity?
}