package net.eiradir.server.process.example

import com.badlogic.ashley.core.Engine
import net.eiradir.server.process.*
import net.eiradir.server.process.task.*

@ProcessDefinitionMarker
fun ProcessDefinitionBuilder.miningInterruptions() {
    pick(
        InformTask("Your pickaxe gets stuck in the ore. You take a moment to pull it out."),
        InformTask("As you strike the node, a stone chip flies towards your face."),
        InformTask("You take a moment to rest."),
        InformTask("You get distracted by something else."),
        InformTask("The pickaxe almost slips from your hands. You regain your grip."),
        InformTask("You get tired. You take a moment to wipe the sweat from your forehead.")
    )
    failure()
}

fun main(args: Array<String>) {
    //define.process("gathering.iron_node") {
    val process = ProcessDefinition("mining_sample").apply {
        timed {
            name = "Mining"
            duration = 2000 // we also have access to the EventContext in here so we could vary duration based on skill/buff
        }
        chance(0.05f) {
            miningInterruptions() // this picks a random inform, calls learn("mining", 1) and calls failure()
        }
        // damageTool()
        // hunger()
        // deplete()
        chance(0.7f) {
            pick(
                ItemTask("stones") to 90,
                ItemTask("iron_ore") to 60
            )
            // learn("mining", 2) // learn twice as fast on success
            success()
        }
    }

    val engine = Engine()
    engine.addSystem(ProcessSystem())

    val entity = engine.createEntity()
    val testComponent = ProcessComponent()
    entity.add(testComponent)
    engine.addEntity(entity)

    var running = true
    while (running) {
        if (testComponent.activeContext == null) {
            testComponent.activeContext = ProcessContext(entity, process).apply {
                onSuccess {
                    println("success")
                }
                onFailure {
                    println("failure")
                    running = false
                }
            }
        }
        engine.update(10f)
        Thread.sleep(10)
    }
}