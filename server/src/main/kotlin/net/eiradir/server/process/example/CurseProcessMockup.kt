package net.eiradir.server.process.example

import com.badlogic.ashley.core.Engine
import net.eiradir.server.process.*
import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.process.entity.ProcessComponent
import net.eiradir.server.process.entity.ProcessSystem
import net.eiradir.server.process.task.*

fun main(args: Array<String>) {
    val process = ProcessDefinition("resume_sample").apply {
        mergeStacks = true
        applied {
            inform("this runs whenever the process is applied (either manually or from loading it back in after logout")
            // this is a synchronous context, e.g. we cannot use timed() in here
        }
        stacked {
            inform("this runs when a process of the same type is applied again (but only if mergeStacks is enabled)")
            // this is a synchronous context, e.g. we cannot use timed() in here
        }

        // this is an asynchronous context, so we can use timed() and others in here
        inform("ayo something wrong")
        //timeout("curseTimer01") {
        //    timeoutState = ProcessState.Pass
        //    duration = 30000
        //    awaitProcess("curse_lifted") {
        //        inform("you feel better phew")
        //        failure()
        //    }
        //}
        either {
            timed("curseTimer01") {
                duration = 30000
            }
//            chain {
//                awaitProcess("curse_lifted")
//                inform("you feel better phew")
//                failure()
//            }
            //awaitProcess("curse_lifted") {
            //    inform("you feel better phew")
            //    failure()
            //}
        }
        checkpoint("curseTimer01Done")
        inform("You feelin kinda poopy uh oh")
        timed("curseTimer02") {
            duration = 30000
        }
        checkpoint("curseTimer02Done")
        inform("oh no you die now")
        // die()
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