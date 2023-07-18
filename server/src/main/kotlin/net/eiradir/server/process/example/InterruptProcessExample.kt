package net.eiradir.server.process.example

import com.badlogic.ashley.core.Engine
import net.eiradir.server.process.*
import net.eiradir.server.process.task.*

fun main(args: Array<String>) {
    val process = ProcessDefinition("interrupt_example").apply {
        initialize {
            fork{
                repeat {
                    chance(0.1f) {
                        interrupt()
                    }
                    timed {
                        duration = 1000
                    }
                }
            }
        }

        interrupted {
            inform("I GOT INTERRUPTED")
            failure()
        }

        repeat {
            inform("I WILL LOOP FOREVER WAHAHAHA unless I get interrupted")
            timed {
                duration = 1000
            }
        }
    }

    val engine = Engine()
    engine.addSystem(ProcessSystem())

    val entity = engine.createEntity()
    val testComponent = ProcessComponent()
    entity.add(testComponent)
    engine.addEntity(entity)

    testComponent.activeContext = ProcessContext(entity, process)

    while (true) {
        engine.update(10f)
        Thread.sleep(10)
    }
}