package net.eiradir.server.process.entity

import com.badlogic.ashley.core.Component
import net.eiradir.server.process.ProcessContext

class ProcessComponent : Component {
    var activeContext: ProcessContext? = null
    val passiveContexts = mutableListOf<ProcessContext>()
}