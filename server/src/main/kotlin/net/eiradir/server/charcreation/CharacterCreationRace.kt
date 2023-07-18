package net.eiradir.server.charcreation

data class CharacterCreationGear(
    val id: Int,
    val isoId: Int,
    val name: String,
    val allowColor: Boolean
)

data class CharacterCreationVisualTrait(
    val id: Int,
    val vistId: Int,
    val name: String
)

data class CharacterCreationRace(
    val id: Int,
    val name: String,
    val maleIsoId: Int,
    val femaleIsoId: Int,
    val maxStatPoints: Int,
    val minStats: Map<String, Int>,
    val minAge: Int,
    val maxAge: Int,
    val gearOptions: Map<String, Collection<CharacterCreationGear>>,
    val visualTraitOptions: Map<String, Collection<CharacterCreationVisualTrait>>,
    val skinColors: List<String>,
    val hairColors: List<String>,
)
