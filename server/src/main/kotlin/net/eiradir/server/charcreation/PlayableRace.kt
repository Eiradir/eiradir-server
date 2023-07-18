package net.eiradir.server.charcreation

import com.badlogic.gdx.graphics.Color
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class PlayableRace(
    override val name: String,
    val maleRaceName: String = "",
    val femaleRaceName: String = "",
    val maxStatPoints: Int = 85,
    val minStats: Map<String, Int> = emptyMap(),
    val minAge: Int = 18,
    val maxAge: Int = 99,
    val gearOptions: Multimap<String, String> = ArrayListMultimap.create(),
    val visualTraitOptions: Multimap<String, String> = ArrayListMultimap.create(),
    val skinColors: Set<Color> = emptySet(),
    val hairColors: Set<Color> = emptySet(),
) : RegistryEntry<PlayableRace> {
    override fun registry(registries: Registries): Registry<PlayableRace> {
        return registries.playableRaces
    }
}