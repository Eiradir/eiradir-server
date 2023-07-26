package net.eiradir.server.process.example

import com.badlogic.ashley.core.Engine
import net.eiradir.server.process.*
import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.process.entity.ProcessComponent
import net.eiradir.server.process.entity.ProcessSystem
import net.eiradir.server.process.task.*

fun main(args: Array<String>) {
    val process = ProcessDefinition("checkpoint_sample").apply {
        inform("starting timer")
        timed {
            name = "Mining"
            duration = 1000 // we also have access to the EventContext in here so we could vary duration based on skill/buff
        }
        inform("timer finished")
        failure()
        inform("this should never be printed")
        checkpoint("after_timer")
        inform("after checkpoint")
    }

    val engine = Engine()
    engine.addSystem(ProcessSystem())

    val entity = engine.createEntity()
    val testComponent = ProcessComponent()
    entity.add(testComponent)
    engine.addEntity(entity)

    testComponent.activeContext = ProcessContext(entity, process).apply {
        onFailure {
            testComponent.activeContext = ProcessContext(entity, process, "after_timer")
        }
    }

    while (true) {
        engine.update(10f)
        Thread.sleep(10)
    }
}