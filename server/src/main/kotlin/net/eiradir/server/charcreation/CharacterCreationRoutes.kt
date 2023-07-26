package net.eiradir.server.charcreation

import com.badlogic.gdx.graphics.Color
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.eiradir.server.auth.EiradirPrincipal
import net.eiradir.server.http.KtorInitializer
import net.eiradir.server.item.InventoryService
import net.eiradir.server.item.ItemDataKeys
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.locale.I18n
import net.eiradir.server.persistence.CharacterStorage
import net.eiradir.server.persistence.InventoryStorage
import net.eiradir.server.persistence.json.JsonCharacter
import net.eiradir.server.player.CharacterSex
import net.eiradir.server.player.InventoryKeys
import net.eiradir.server.player.TraitInstanceData
import net.eiradir.server.player.VisualTraitInstanceData
import net.eiradir.server.registry.Registries

class CharacterCreationRoutes(
    private val registries: Registries,
    private val characterStorage: CharacterStorage,
    private val inventoryStorage: InventoryStorage,
    private val inventoryService: InventoryService,
    private val i18n: I18n
) : KtorInitializer {

    override fun Routing.configureRoutes() {
        get("character_creation") {
            val races = registries.playableRaces.getAll().map { race ->
                val gearOptions = race.gearOptions.asMap().mapValues { entry ->
                    entry.value.map {
                        val item = registries.items.getByName(it) ?: registries.items.invalid(it)
                        CharacterCreationGear(item.id(registries) ?: 0, item.isoId(registries), i18n.get("item.${item.name}"), true)
                    }
                }
                val visualTraitOptions = race.visualTraitOptions.asMap().mapValues { entry ->
                    entry.value.map { vistName ->
                        val vistId = registries.idResolver.resolve("vists", vistName) ?: 0
                        CharacterCreationVisualTrait(vistId, vistId, i18n.get("vist.$vistName"))
                    }
                }
                val maleRace = registries.races.getByName(race.maleRaceName) ?: registries.races.invalid(race.maleRaceName)
                val femaleRace = registries.races.getByName(race.femaleRaceName) ?: registries.races.invalid(race.femaleRaceName)
                CharacterCreationRace(
                    race.id(registries) ?: 0,
                    i18n.get("race.${race.name}"),
                    registries.idResolver.resolve("isos", maleRace.isoName) ?: 0,
                    registries.idResolver.resolve("isos", femaleRace.isoName) ?: 0,
                    race.maxStatPoints,
                    race.minStats,
                    race.minAge,
                    race.maxAge,
                    gearOptions,
                    visualTraitOptions,
                    race.skinColors.map { it.toString() },
                    race.hairColors.map { it.toString() }
                )
            }
            val traits = registries.traits.getAll().stream().filter { it.availableAtCreation }.map { trait ->
                CharacterCreationTrait(
                    trait.id(registries) ?: 0,
                    i18n.get("trait.${trait.name}"),
                    i18n.get("trait.${trait.name}.description"),
                    trait.category,
                    trait.valence,
                    trait.providesTraits.map { registries.traits.getByName(it)?.id(registries) ?: 0 }
                )
            }.toList()
            call.respond(CharacterCreationOptionsResponse(races, traits))
        }
        authenticate {
            post("characters") {
                val principal = call.principal<EiradirPrincipal>()!!
                val data = call.receive<CharacterCreationRequest>()
                val playableRace = registries.playableRaces.getById(data.raceId) ?: throw BadRequestException("Invalid race id")
                val race = registries.races.getByName(if (data.sex == CharacterSex.Male) playableRace.maleRaceName else playableRace.femaleRaceName)
                    ?: throw IllegalStateException("Failed to lookup race")
                val raceId = race.id(registries) ?: throw IllegalStateException("Failed to lookup race id")
                val chara = JsonCharacter(principal.accountId, 0, data.name, raceId)
                // TODO validate skin color
                chara.skinColor = data.skinColor
                // TODO validate attributes
                chara.stats[registries.stats.getId("age")!!] = data.age
                chara.stats[registries.stats.getId("strength")!!] = data.strength
                chara.stats[registries.stats.getId("dexterity")!!] = data.dexterity
                chara.stats[registries.stats.getId("agility")!!] = data.agility
                chara.stats[registries.stats.getId("constitution")!!] = data.constitution
                chara.stats[registries.stats.getId("intelligence")!!] = data.intelligence
                chara.stats[registries.stats.getId("perception")!!] = data.perception
                chara.stats[registries.stats.getId("arcanum")!!] = data.arcanum
                // TODO validate visual traits
                chara.visualTraits.addAll(data.visualTraits.map { VisualTraitInstanceData(it.id, it.color) })
                // TODO validate gear
                val inventory = inventoryStorage.createInventory(18)
                data.gear.forEach { gear ->
                    val item = registries.items.getById(gear.id) ?: throw BadRequestException("Invalid item id")
                    val itemInstance = ItemInstance(item)
                    itemInstance.setData(ItemDataKeys.COLOR, Color(gear.color).toString())
                    itemInstance.setData(ItemDataKeys.SPAWN_ITEM, "true")
                    var foundSlot = false
                    for (slotId in item.equipmentSlot.slotIds) {
                        if (inventory.getItem(slotId).isEmpty) {
                            inventory.setItem(slotId, itemInstance)
                            foundSlot = true
                        }
                    }
                    if (!foundSlot) {
                        inventoryService.addItemToInventory(inventory, itemInstance)
                    }
                }
                chara.inventoryIds[InventoryKeys.DEFAULT] = inventory.id
                inventoryStorage.save(inventory)
                // TODO validate traits
                chara.traits.addAll(data.traits.map { TraitInstanceData(it, emptyMap()) })
                val id = characterStorage.save(chara)
                call.respond(CharacterCreationResponse(id))
            }
        }
    }
}