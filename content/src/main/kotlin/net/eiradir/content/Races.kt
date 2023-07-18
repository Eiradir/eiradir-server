package net.eiradir.content

import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.RegistryBuilders

class Races(define: RegistryBuilders) : Initializer {
    init {
        define.race("human_male")
        define.race("human_female")
        define.race("elf_male")
        define.race("elf_female")
        define.race("dwarf_male")
        define.race("dwarf_female")
        define.race("orc_male")
        define.race("orc_female")
        define.race("silve_male")
        define.race("silve_female")
        define.race("chicken")
        define.race("cow")
        define.race("pig")
        define.race("sheep")
    }
}