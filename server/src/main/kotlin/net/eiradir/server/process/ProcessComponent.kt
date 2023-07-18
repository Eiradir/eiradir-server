package net.eiradir.server.process

import com.badlogic.ashley.core.Component

class ProcessComponent : Component {
    var activeContext: ProcessContext? = null
    val passiveContexts = mutableListOf<ProcessContext>()
}