package net.eiradir.content

import net.eiradir.server.data.Tile
import net.eiradir.server.data.TransitionMode
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.RegistryBuilders

class Tiles(define: RegistryBuilders) : Initializer {
    init {
        define.tile(Tile.Invalid.name)
        define.tile(Tile.Clear.name)
        define.tile("water") {
            transitionMode = TransitionMode.CannotTransition
        }
        define.tile("grass_01")
        define.tile("tundra_01")
        define.tile("tundra_02")
        define.tile("forest_ground")
        define.tile("snow")
        define.tile("forest_ground_taiga_north")
        define.tile("forest_ground_caeril_01")
        define.tile("forest_ground_caeril_02")
        define.tile("meadows_01")
        define.tile("swamp")
        define.tile("swamp_darkon_01")
        define.tile("swamp_darkon_02")
        define.tile("swamp_hemara_01")
        define.tile("swamp_hemara_02")
        define.tile("rocky_grass")
        define.tile("swamp_wetland")
        define.tile("meadows_02")
        define.tile("meadows_03")
        define.tile("swamp_mud")
        define.tile("mountains")
        define.tile("sand_north")
        define.tile("dirt_caeril")
        define.tile("sand_south")
        define.tile("dry_grass")
        define.tile("savannah_01")
        define.tile("savannah_02")
        define.tile("savannah_03")
        define.tile("savannah_04")
        define.tile("steppe_01")
        define.tile("steppe_02")
        define.tile("grass_02")
        define.tile("grass_03")
        define.tile("lawn")
        define.tile("forest_darkon")
        define.tile("forest_hemara")
        define.tile("red_forest_caeril_01")
        define.tile("red_forest_caeril_02")
        define.tile("red_forest_caeril_03")
        define.tile("red_forest_caeril_04")
        define.tile("red_forest_caeril_05")
        define.tile("red_forest_caeril_06")
        define.tile("grass_darkon")
        define.tile("gravel")
        define.tile("marsh")
        define.tile("meadows_darkon")
        define.tile("mire")
        define.tile("moor_darkon")
        define.tile("moor_01")
        define.tile("moor_02")
        define.tile("moor_03")
        define.tile("moor_04")
        define.tile("moor_05")
        define.tile("mud")
        define.tile("pebbles_01")
        define.tile("pebbles_02")
        define.tile("rocks")
        define.tile("stone_01")
        define.tile("stone_02")
        define.tile("stone_03")
        define.tile("stone_04")
        define.tile("stone_05")
        define.tile("stone_06")
        define.tile("stone_07")
        define.tile("stone_08")
        define.tile("dirt")
        define.tile("forest_ground_caeril_03")
        define.tile("dark_wooden_rooftop") {
            transitionMode = TransitionMode.RefuseTransitions
        }
        define.tile("floor_wood_01") {
            transitionMode = TransitionMode.RefuseTransitions
        }
        define.tile("floor_wood_02") {
            transitionMode = TransitionMode.RefuseTransitions
        }

        define.transitions {
            tile("grass_01").transitionsOnto("rocks")
        }
    }
}