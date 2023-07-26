package net.eiradir.content

import net.eiradir.server.plugin.Initializer
import net.eiradir.server.process.task.buff
import net.eiradir.server.process.task.inform
import net.eiradir.server.process.task.repeat
import net.eiradir.server.process.task.timed
import net.eiradir.server.registry.RegistryBuilders
import net.eiradir.server.stats.ConstantBonus

class ProcessTypes(define: RegistryBuilders, statTypes: StatTypes) : Initializer {
    init {
        define.process("poison") {
            repeat {
                inform("poison")
                timed {
                    duration = 1000
                }
            }
        }

        define.process("second_wind") {
            buff(statTypes.health, ConstantBonus(3000))
            timed {
                duration = 3000
            }
            buff(statTypes.health, ConstantBonus(-1500))
            timed {
                duration = 2000
            }
        }

        define.process("test_spells") {
            buff("spell", ConstantBonus(1))
            timed {
                duration = 2000
            }
        }
    }
}