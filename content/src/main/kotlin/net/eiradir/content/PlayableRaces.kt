package net.eiradir.content

import com.badlogic.gdx.graphics.Color
import net.eiradir.server.charcreation.PlayableRaceBuilder
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.RegistryBuilders

class PlayableRaces(define: RegistryBuilders) : Initializer {

    val GEAR_HATS = "hats"
    val GEAR_ROBES = "robes"
    val GEAR_SHIRTS = "shirts"
    val GEAR_PANTS = "pants"
    val GEAR_SHOES = "shoes"
    val GEAR_WEAPONS = "weapons"

    val VIST_HAIR = "hairs"
    val VIST_BEARD = "beards"

    init {
        define.playableRace("human") {
            maleRaceName = "human_male"
            femaleRaceName = "human_female"
            minStat("strength", 2)
            minStat("dexterity", 3)
            minStat("constitution", 3)
            minStat("perception", 3)
            minStat("agility", 3)
            minStat("intelligence", 3)
            minStat("arcanum", 2)

            skinColor(Color(0.96f, 0.87f, 0.7f, 1f)) // Light Skin
            skinColor(Color(0.85f, 0.72f, 0.62f, 1f)) // Fair Skin
            skinColor(Color(0.75f, 0.64f, 0.52f, 1f)) // Light Medium Skin
            skinColor(Color(0.65f, 0.56f, 0.44f, 1f)) // Medium Skin
            skinColor(Color(0.6f, 0.51f, 0.4f, 1f)) // Olive Skin
            skinColor(Color(0.5f, 0.42f, 0.33f, 1f)) // Tan Skin
            skinColor(Color(0.45f, 0.37f, 0.29f, 1f)) // Dark Tan Skin
            skinColor(Color(0.4f, 0.32f, 0.24f, 1f)) // Light Brown Skin
            skinColor(Color(0.35f, 0.27f, 0.2f, 1f)) // Brown Skin
            skinColor(Color(0.3f, 0.22f, 0.16f, 1f)) // Dark Brown Skin
            skinColor(Color(0.25f, 0.17f, 0.12f, 1f)) // Very Dark Brown Skin
            skinColor(Color(0.94f, 0.78f, 0.72f, 1f)) // Pale Pink Skin
            skinColor(Color(0.98f, 0.87f, 0.78f, 1f)) // Light Pink Skin
            skinColor(Color(0.96f, 0.64f, 0.38f, 1f)) // Medium Pink Skin
            skinColor(Color(0.93f, 0.46f, 0.24f, 1f)) // Dark Pink Skin
            skinColor(Color(0.92f, 0.78f, 0.56f, 1f)) // Pale Yellow Skin
            skinColor(Color(0.96f, 0.86f, 0.56f, 1f)) // Light Yellow Skin
            skinColor(Color(0.98f, 0.92f, 0.84f, 1f)) // Medium Yellow Skin
            skinColor(Color(0.93f, 0.83f, 0.7f, 1f)) // Dark Yellow Skin
            skinColor(Color(0.91f, 0.76f, 0.65f, 1f)) // Olive Yellow Skin

            defaultHairColors()
            defaultGear()

            visualTrait(VIST_HAIR, "short_hair")
            visualTrait(VIST_HAIR, "shoulder_length_hair")
        }

        define.playableRace("elf") {
            maleRaceName = "elf_male"
            femaleRaceName = "elf_female"
            minStat("strength", 1)
            minStat("dexterity", 3)
            minStat("constitution", 1)
            minStat("perception", 3)
            minStat("agility", 3)
            minStat("intelligence", 5)
            minStat("arcanum", 5)

            skinColor(Color(0.9f, 0.8f, 0.76f, 1f)) // Pale beige
            skinColor(Color(0.98f, 0.92f, 0.84f, 1f)) // Light cream
            skinColor(Color(0.86f, 0.78f, 0.7f, 1f)) // Fair beige
            skinColor(Color(0.78f, 0.7f, 0.62f, 1f)) // Warm beige
            skinColor(Color(0.9f, 0.85f, 0.8f, 1f)) // Pale peach
            skinColor(Color(0.94f, 0.87f, 0.8f, 1f)) // Light peach
            skinColor(Color(0.88f, 0.78f, 0.7f, 1f)) // Fair peach
            skinColor(Color(0.8f, 0.7f, 0.6f, 1f)) // Warm peach
            skinColor(Color(0.75f, 0.65f, 0.55f, 1f)) // Light brown
            skinColor(Color(0.6f, 0.5f, 0.4f, 1f)) // Medium brown
            skinColor(Color(0.45f, 0.35f, 0.25f, 1f)) // Dark brown
            skinColor(Color(0.65f, 0.6f, 0.5f, 1f)) // Olive light
            skinColor(Color(0.5f, 0.45f, 0.35f, 1f)) // Olive medium
            skinColor(Color(0.35f, 0.3f, 0.2f, 1f)) // Olive dark
            skinColor(Color(0.95f, 0.92f, 0.88f, 1f)) // Pale golden
            skinColor(Color(0.88f, 0.8f, 0.72f, 1f)) // Light golden
            skinColor(Color(0.7f, 0.6f, 0.5f, 1f)) // Warm golden

            defaultHairColors()
            defaultGear()

            visualTrait(VIST_HAIR, "braid")
            visualTrait(VIST_HAIR, "shoulder_length_hair")
        }

        define.playableRace("dwarf") {
            maleRaceName = "dwarf_male"
            femaleRaceName = "dwarf_female"
            minStat("strength", 5)
            minStat("dexterity", 3)
            minStat("constitution", 5)
            minStat("perception", 3)
            minStat("agility", 2)
            minStat("intelligence", 1)
            minStat("arcanum", 1)

            skinColor(Color(0.9f, 0.7f, 0.6f, 1f)) // Lightest Tan
            skinColor(Color(0.85f, 0.65f, 0.55f, 1f)) // Very Light Tan
            skinColor(Color(0.8f, 0.6f, 0.5f, 1f)) // Light Tan
            skinColor(Color(0.75f, 0.55f, 0.45f, 1f)) // Medium-Light Tan
            skinColor(Color(0.7f, 0.5f, 0.4f, 1f)) // Tan
            skinColor(Color(0.65f, 0.45f, 0.35f, 1f)) // Medium Tan
            skinColor(Color(0.6f, 0.4f, 0.3f, 1f)) // Dark Tan
            skinColor(Color(0.8f, 0.65f, 0.55f, 1f)) // Light Peach
            skinColor(Color(0.75f, 0.6f, 0.5f, 1f)) // Peach
            skinColor(Color(0.7f, 0.55f, 0.45f, 1f)) // Dark Peach
            skinColor(Color(0.85f, 0.7f, 0.6f, 1f)) // Light Rose
            skinColor(Color(0.65f, 0.5f, 0.4f, 1f)) // Medium-Light Brown
            skinColor(Color(0.6f, 0.45f, 0.35f, 1f)) // Brown
            skinColor(Color(0.55f, 0.4f, 0.3f, 1f)) // Medium Brown
            skinColor(Color(0.5f, 0.35f, 0.25f, 1f)) // Dark Brown
            skinColor(Color(0.45f, 0.3f, 0.2f, 1f)) // Very Dark Brown
            skinColor(Color(0.4f, 0.25f, 0.15f, 1f)) // Darkest Brown

            defaultHairColors()
            defaultGear()

            visualTrait(VIST_HAIR, "braids")
            visualTrait(VIST_HAIR, "chin_length_hair")

            visualTrait(VIST_BEARD, "garibaldi")
        }

        define.playableRace("orc") {
            maleRaceName = "orc_male"
            femaleRaceName = "orc_female"
            minStat("strength", 5)
            minStat("dexterity", 2)
            minStat("constitution", 4)
            minStat("perception", 2)
            minStat("agility", 5)
            minStat("intelligence", 1)
            minStat("arcanum", 1)

            skinColor(Color(0.13f, 0.55f, 0.13f, 1f)) // Dark Green
            skinColor(Color(0.20f, 0.70f, 0.20f, 1f)) // Green
            skinColor(Color(0.27f, 0.85f, 0.27f, 1f)) // Light Green
            skinColor(Color(0.23f, 0.55f, 0.23f, 1f)) // Dark Olive Green
            skinColor(Color(0.42f, 0.56f, 0.14f, 1f)) // Olive Drab
            skinColor(Color(0.56f, 0.73f, 0.56f, 1f)) // Dark Sea Green
            skinColor(Color(0.33f, 0.42f, 0.18f, 1f)) // Olive
            skinColor(Color(0.41f, 0.55f, 0.41f, 1f)) // Dark Olive Green
            skinColor(Color(0.61f, 0.51f, 0.31f, 1f)) // Dark Khaki
            skinColor(Color(0.73f, 0.53f, 0.40f, 1f)) // Tan
            skinColor(Color(0.54f, 0.27f, 0.07f, 1f)) // Saddle Brown
            skinColor(Color(0.65f, 0.49f, 0.24f, 1f)) // Goldenrod
            skinColor(Color(0.72f, 0.52f, 0.04f, 1f)) // Dark Goldenrod
            skinColor(Color(0.37f, 0.25f, 0.13f, 1f)) // Dark Brown
            skinColor(Color(0.59f, 0.44f, 0.09f, 1f)) // Peru
            skinColor(Color(0.75f, 0.61f, 0.48f, 1f)) // Rosy Brown
            skinColor(Color(0.80f, 0.72f, 0.62f, 1f)) // Light Brown
            skinColor(Color(0.50f, 0.50f, 0.50f, 1f)) // Grey
            skinColor(Color(0.66f, 0.66f, 0.66f, 1f)) // Silver
            skinColor(Color(0.86f, 0.86f, 0.86f, 1f)) // Light Grey

            defaultHairColors()
            defaultGear()

            visualTrait(VIST_HAIR, "braid")
        }

        define.playableRace("silve") {
            maleRaceName = "silve_male"
            femaleRaceName = "silve_female"
            minStat("strength", 6)
            minStat("dexterity", 1)
            minStat("constitution", 3)
            minStat("perception", 4)
            minStat("agility", 5)
            minStat("intelligence", 2)
            minStat("arcanum", 2)

            skinColor(Color(0.804f, 0.522f, 0.247f, 1f)) // Brown
            skinColor(Color(0.663f, 0.431f, 0.212f, 1f)) // Dark Brown
            skinColor(Color(0.875f, 0.675f, 0.474f, 1f)) // Tan
            skinColor(Color(0.722f, 0.525f, 0.043f, 1f)) // Gold
            skinColor(Color(0.545f, 0.271f, 0.075f, 1f)) // Bronze
            skinColor(Color(0.769f, 0.647f, 0.471f, 1f)) // Beige
            skinColor(Color(0.824f, 0.706f, 0.549f, 1f)) // Light Beige
            skinColor(Color(0.647f, 0.443f, 0.157f, 1f)) // Dark Gold
            skinColor(Color(0.902f, 0.745f, 0.569f, 1f)) // Pale
            skinColor(Color(0.596f, 0.459f, 0.329f, 1f)) // Rust
            skinColor(Color(0.914f, 0.769f, 0.416f, 1f)) // Light Gold
            skinColor(Color(0.702f, 0.537f, 0.365f, 1f)) // Sienna
            skinColor(Color(0.722f, 0.49f, 0.18f, 1f)) // Dark Sienna
            skinColor(Color(0.824f, 0.706f, 0.549f, 1f)) // Light Tan
            skinColor(Color(0.78f, 0.596f, 0.365f, 1f)) // Dark Tan
            skinColor(Color(0.69f, 0.565f, 0.427f, 1f)) // Dark Beige
            skinColor(Color(0.745f, 0.6f, 0.4f, 1f)) // Amber
            skinColor(Color(0.706f, 0.549f, 0.376f, 1f)) // Dark Amber
            skinColor(Color(0.827f, 0.71f, 0.549f, 1f)) // Light Amber
            skinColor(Color(0.722f, 0.565f, 0.4f, 1f)) // Medium Amber
            skinColor(Color(0.788f, 0.6f, 0.337f, 1f)) // Medium Gold

            defaultHairColors()
            defaultGear()
        }
    }

    private fun PlayableRaceBuilder.defaultHairColors() {
        hairColor(Color(0.2f, 0.2f, 0.2f, 1f)) // Dark Gray
        hairColor(Color(0.4f, 0.4f, 0.4f, 1f)) // Gray
        hairColor(Color(0.6f, 0.6f, 0.6f, 1f)) // Light Gray
        hairColor(Color(1.0f, 1.0f, 1.0f, 1f)) // White
        hairColor(Color(0.71f, 0.4f, 0.11f, 1f)) // Auburn
        hairColor(Color(0.39f, 0.28f, 0.07f, 1f)) // Brown
        hairColor(Color(0.78f, 0.47f, 0.15f, 1f)) // Light Brown
        hairColor(Color(0.65f, 0.16f, 0.16f, 1f)) // Red
        hairColor(Color(0.62f, 0.47f, 0.35f, 1f)) // Chestnut
        hairColor(Color(0.49f, 0.31f, 0.16f, 1f)) // Dark Blonde
        hairColor(Color(0.84f, 0.67f, 0.51f, 1f)) // Blonde
        hairColor(Color(0.96f, 0.88f, 0.66f, 1f)) // Light Blonde
        hairColor(Color(0.35f, 0.25f, 0.18f, 1f)) // Brunette
        hairColor(Color(0.2f, 0.11f, 0.05f, 1f)) // Dark Brown
        hairColor(Color(0.98f, 0.84f, 0.65f, 1f)) // Golden Blonde
        hairColor(Color(0.68f, 0.5f, 0.3f, 1f)) // Caramel
        hairColor(Color(0.62f, 0.44f, 0.29f, 1f)) // Honey
        hairColor(Color(0.93f, 0.82f, 0.62f, 1f)) // Light Caramel
    }

    private fun PlayableRaceBuilder.defaultGear() {
        gear(GEAR_HATS, "cloth_hat")
        gear(GEAR_HATS, "wizard_hat")
        gear(GEAR_HATS, "wizard_hood")
        gear(GEAR_HATS, "warm_fur_hat")
        gear(GEAR_HATS, "stylish_hat")
        gear(GEAR_HATS, "peasant_hat")
        gear(GEAR_HATS, "hood")
        gear(GEAR_HATS, "festive_hat")
        gear(GEAR_HATS, "arme_helmet")
        gear(GEAR_HATS, "basinet")
        gear(GEAR_HATS, "jawbone_sallet")
        gear(GEAR_HATS, "leather_cap")
        gear(GEAR_HATS, "sallet")

        gear(GEAR_ROBES, "scholar_robe")
        gear(GEAR_ROBES, "red_cloak")
        gear(GEAR_ROBES, "ranger_cloak")
        gear(GEAR_ROBES, "priest_robe")
        gear(GEAR_ROBES, "merchant_cloak")
        gear(GEAR_ROBES, "mage_robe")
        gear(GEAR_ROBES, "long_cloak")
        gear(GEAR_ROBES, "fitted_tunic")
        gear(GEAR_ROBES, "dress")
        gear(GEAR_ROBES, "cultist_robe")

        gear(GEAR_SHIRTS, "tunic")
        gear(GEAR_SHIRTS, "shirt")
        gear(GEAR_SHIRTS, "shirt_02")
        gear(GEAR_SHIRTS, "shirt_03")
        gear(GEAR_SHIRTS, "shirt_04")
        gear(GEAR_SHIRTS, "shirt_05")
        gear(GEAR_SHIRTS, "shirt_06")
        gear(GEAR_SHIRTS, "blouse_01")
        gear(GEAR_SHIRTS, "blouse_02")
        gear(GEAR_SHIRTS, "blouse_03")
        gear(GEAR_SHIRTS, "blouse_04")
        gear(GEAR_SHIRTS, "blouse_05")
        gear(GEAR_SHIRTS, "blouse_06")
        gear(GEAR_SHIRTS, "noble_shirt")
        gear(GEAR_SHIRTS, "jack")
        gear(GEAR_SHIRTS, "fur_jack")
        gear(GEAR_SHIRTS, "light_plate_cuirass")
        gear(GEAR_SHIRTS, "hunting_jacket")
        gear(GEAR_SHIRTS, "light_leather_armor")
        gear(GEAR_SHIRTS, "full_leather_armor")
        gear(GEAR_SHIRTS, "studded_mercenary_armor")
        gear(GEAR_SHIRTS, "heavy_knight_plate")

        gear(GEAR_PANTS, "trousers")
        gear(GEAR_PANTS, "fur_leggings")
        gear(GEAR_PANTS, "steel_leggings_greaves")
        gear(GEAR_PANTS, "short_leather_leggings")
        gear(GEAR_PANTS, "leather_leggings")

        gear(GEAR_SHOES, "leather_shoes")
        gear(GEAR_SHOES, "leather_boots")
        gear(GEAR_SHOES, "steel_sabatons")

        gear(GEAR_WEAPONS, "short_sword")
    }
}