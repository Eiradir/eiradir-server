package net.eiradir.content

import net.eiradir.server.plugin.Initializer
import net.eiradir.server.process.task.item
import net.eiradir.server.process.task.timed
import net.eiradir.server.registry.RegistryBuilders

class ItemInteractions(define: RegistryBuilders, items: Items, interactions: Interactions) : Initializer {
    init {
        val fruitPicking = define.process("picking_fruit") {
            timed {
                duration = 1000
            }
            item("apple", 1)
        }

        val pickFruits = define.interaction("pick_fruits") {
            handle {
                it.process(fruitPicking)?.onSuccess {
                    it.repeat()
                }
            }
        }

        define.interactable("fruit_tree", pickFruits)

        define.interactable(items.rubyAmulet, interactions.eat)

        define.interactable(items.beehiveActive, interactions.interact) {
            handle {
                print("beehive used")
            }
        }

        define.interactable(items.beehiveActive, interactions.eat) {
            handle {
                print("omG you are eating BEEEEES")
            }
            conditionally {
                true
            }
            requires {
                true
            }
        }
    }
}