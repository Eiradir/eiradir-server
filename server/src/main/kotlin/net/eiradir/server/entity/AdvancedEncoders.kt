package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.*
import net.eiradir.server.entity.network.NetworkedDataKey
import net.eiradir.server.io.SupportedByteBuf
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.writeEnum
import net.eiradir.server.item.ItemDataKeys
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.registry.Registries
import net.eiradir.server.trait.TraitDataKeys
import net.eiradir.server.trait.TraitInstance

class AdvancedEncoders(private val registries: Registries) {

    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<GridTransform>()
    private val isoMapper = mapperFor<IsoComponent>()

    fun createNetworkedEntity(entity: Entity): NetworkedEntity {
        val id = idMapper[entity]?.id ?: throw IllegalStateException("Entity does not have an ID")
        val transform = transformMapper[entity] ?: throw IllegalStateException("Entity does not have a GridTransform")
        val iso = isoMapper[entity] ?: throw IllegalStateException("Entity does not have an IsoComponent")
        val networkedEntity = NetworkedEntity(id, transform.position, transform.direction, iso.isoId)
        for (component in entity.components) {
            // TODO allow me to register this instead of having a hardcoded switch
            when (component) {
                is NameComponent -> {
                    networkedEntity.data[NetworkedDataKey.NAME] = component.name // TODO this should respect #i
                }

                is ColorComponent -> {
                    networkedEntity.data[NetworkedDataKey.COLOR] = component.color
                }

                is VisualTraitsComponent -> {
                    networkedEntity.data[NetworkedDataKey.VISUAL_TRAITS] = component.visualTraits.stream().mapToInt { it.id }.toArray()
                    networkedEntity.data[NetworkedDataKey.VISUAL_TRAIT_COLORS] =
                        component.visualTraits.stream().map { Color(it.color) }.toArray { arrayOfNulls<Color>(it) }
                }

                is InventoryComponent -> {
                    val inventory = component.defaultInventory ?: continue
                    val paperdolls = mutableListOf<Int>()
                    val paperdollColors = mutableListOf<Color>()
                    inventory.items.withIndex().forEach { (index, it) ->
                        if (it.isNotEmpty && it.item.equipmentSlot.slotIds.contains(index)) {
                            paperdolls.add(it.item.isoId(registries))
                            paperdollColors.add(it.color)
                        }
                    }
                    networkedEntity.data[NetworkedDataKey.PAPERDOLLS] = paperdolls.toIntArray()
                    networkedEntity.data[NetworkedDataKey.PAPERDOLL_COLORS] = paperdollColors.toTypedArray()
                }
            }
        }
        return networkedEntity
    }

    fun encodeEntity(buf: SupportedOutput, networkedEntity: NetworkedEntity) {
        buf.writeUniqueId(networkedEntity.uniqueId)
        buf.writeVector3Int(networkedEntity.position)
        buf.writeEnum(networkedEntity.direction)
        buf.writeShort(networkedEntity.isoId)
        buf.writeByte(networkedEntity.data.size)
        for ((key, value) in networkedEntity.data) {
            buf.writeEnum(key)
            when (key) {
                NetworkedDataKey.NAME -> buf.writeString(value as String)
                NetworkedDataKey.COLOR -> buf.writeColor(value as Color)
                NetworkedDataKey.PAPERDOLLS -> {
                    val paperdolls = value as IntArray
                    buf.writeByte(paperdolls.size)
                    for (paperdollId in paperdolls) {
                        buf.writeShort(paperdollId)
                    }
                }

                NetworkedDataKey.PAPERDOLL_COLORS -> {
                    val colors = value as Array<Color>
                    buf.writeByte(colors.size)
                    for (color in colors) {
                        buf.writeColor(color)
                    }
                }

                NetworkedDataKey.VISUAL_TRAITS -> {
                    val vists = value as IntArray
                    buf.writeByte(vists.size)
                    for (vistId in vists) {
                        buf.writeShort(vistId)
                    }
                }

                NetworkedDataKey.VISUAL_TRAIT_COLORS -> {
                    val colors = value as Array<Color>
                    buf.writeByte(colors.size)
                    for (color in colors) {
                        buf.writeColor(color)
                    }
                }

                NetworkedDataKey.DURATION -> buf.writeInt(value as Int)

                NetworkedDataKey.CUSTOM -> {
                    val (stringKey, stringValue) = value as Pair<String, String>
                    buf.writeString(stringKey)
                    buf.writeString(stringValue)
                }
            }
        }
    }

    fun decodeEntity(buf: SupportedInput): NetworkedEntity {
        TODO()
    }

    fun decodeItemInstance(buf: SupportedByteBuf): ItemInstance {
        val count = buf.readShort().toInt()
        if (count > 0) {
            val item = buf.readFromRegistry { it.items }
            // TODO this doesn't read item data yet
            return ItemInstance(item, count)
        } else {
            return ItemInstance.Empty
        }
    }

    fun encodeItemInstance(buf: SupportedByteBuf, item: ItemInstance) {
        buf.writeShort(item.count)
        if (item.count > 0) {
            buf.writeShort(item.item.isoId(registries))

            val data = mutableMapOf<NetworkedDataKey, Any>()
            for ((key, value) in item.data) {
                when {
                    key == ItemDataKeys.COLOR -> {
                        data[NetworkedDataKey.COLOR] = Color.valueOf(value)
                    }

                    key.startsWith("c_") -> {
                        data[NetworkedDataKey.CUSTOM] = key to value
                    }
                }
            }

            buf.writeByte(data.size)
            for ((key, value) in data) {
                buf.writeEnum(key)
                when(key) {
                    NetworkedDataKey.COLOR -> {
                        buf.writeColor(value as Color)
                    }

                    NetworkedDataKey.CUSTOM -> {
                        val (stringKey, stringValue) = value as Pair<String, String>
                        buf.writeString(stringKey)
                        buf.writeString(stringValue)
                    }
                    else -> throw IllegalStateException("Unknown item data key $key")
                }
            }
        }
    }

    fun encodeTraitInstance(buf: SupportedByteBuf, trait: TraitInstance) {
        buf.writeShort(trait.trait.iconId(registries))
        val data = mutableMapOf<NetworkedDataKey, Any>()
        for ((key, value) in trait.data) {
            when {
                key == TraitDataKeys.COLOR -> {
                    data[NetworkedDataKey.COLOR] = Color.valueOf(value)
                }

                key == TraitDataKeys.DURATION -> {
                    data[NetworkedDataKey.DURATION] = value.toIntOrNull() ?: 0
                }

                key.startsWith("c_") -> {
                    data[NetworkedDataKey.CUSTOM] = key to value
                }
            }
        }

        buf.writeByte(data.size)
        for ((key, value) in data) {
            buf.writeEnum(key)
            when(key) {
                NetworkedDataKey.COLOR -> {
                    buf.writeColor(value as Color)
                }

                NetworkedDataKey.DURATION -> {
                    buf.writeInt(value as Int)
                }

                NetworkedDataKey.CUSTOM -> {
                    val (stringKey, stringValue) = value as Pair<String, String>
                    buf.writeString(stringKey)
                    buf.writeString(stringValue)
                }
                else -> throw IllegalStateException("Unknown trait data key $key")
            }
        }
    }
}