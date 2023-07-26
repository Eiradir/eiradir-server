package net.eiradir.server.map

import com.google.common.eventbus.EventBus
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import ktx.ashley.mapperFor
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.EntityCommandSource
import net.eiradir.server.commands.argument
import net.eiradir.server.commands.arguments.TileArgument
import net.eiradir.server.commands.arguments.Vector3IntArgument
import net.eiradir.server.commands.literal
import net.eiradir.server.data.Tile
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.MapReference
import net.eiradir.server.entity.event.EntitySwitchedMapEvent
import net.eiradir.server.map.event.TileUpdatedEvent
import net.eiradir.server.map.tilemap.PersistentChunkedMap
import net.eiradir.server.map.view.EditableMapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.nature.NatureGenerator
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.Registries

class MapCommands(
    dispatcher: CommandDispatcher<CommandSource>,
    registries: Registries,
    mapService: MapService,
    mapManager: MapManager,
    scopedMapManager: ScopedMapManager,
    natureGenerator: NatureGenerator,
    private val eventBus: EventBus
) : Initializer {

    private val idMapper = mapperFor<IdComponent>()
    private val mapReferenceMapper = mapperFor<MapReference>()

    init {
        dispatcher.register(
            literal("tile").then(
                literal("set").then(
                    argument("tile", TileArgument.tile(registries)).executes {
                        setTileAt(it.source, it.source.position, TileArgument.getTile(it, "tile"))
                    }.then(
                        argument("pos", Vector3IntArgument.vector3Int()).executes {
                            setTileAt(it.source, Vector3IntArgument.getVector3Int(it, "pos"), TileArgument.getTile(it, "tile"))
                        })
                )
            ).then(
                literal("remove").executes {
                    setTileAt(it.source, it.source.position, null)
                }.then(
                    argument("pos", Vector3IntArgument.vector3Int()).executes {
                        setTileAt(it.source, Vector3IntArgument.getVector3Int(it, "pos"), null)
                    })
            ).then(
                literal("get").executes {
                    getTileAt(it.source, it.source.position)
                }.then(
                    argument("pos", Vector3IntArgument.vector3Int()).executes {
                        getTileAt(it.source, Vector3IntArgument.getVector3Int(it, "pos"))
                    })
            ).then(
                literal("fill").then(
                    argument("tile", TileArgument.tile(registries)).executes {
                        val mapView = it.source.mapView
                        if (mapView !is EditableMapView) {
                            it.source.respond("Cannot edit maps from this command source")
                            return@executes 0
                        }

                        var updated = 0
                        val chunk = mapManager.dimensions.of(it.source.position)
                        for (x in 0..chunk.size) {
                            for (y in 0..chunk.size) {
                                val position = Vector3Int(chunk.toAbsoluteX(x), chunk.toAbsoluteY(y), 0)
                                updated += setTileAt(it.source, position, TileArgument.getTile(it, "tile"))
                            }
                        }
                        updated
                    }
                )
            )
        )

        dispatcher.register(
            literal("nature").executes {
                val mapView = it.source.mapView
                if (mapView !is EditableMapView) {
                    it.source.respond("Cannot edit maps from this command source")
                    return@executes 0
                }

                val map = mapView.getActionableMap(it.source.position)
                if (map == null) {
                    it.source.respond("Actionable map is not loaded")
                    return@executes 0
                }

                (map.chunkedMap as? PersistentChunkedMap)?.loadAllChunks()
                map.getLoadedChunks().forEach { chunk ->
                    natureGenerator.generate(map, chunk.dimensions)
                }
                return@executes 1
            }.then(
                argument("pos", Vector3IntArgument.vector3Int()).executes {
                    val mapView = it.source.mapView
                    if (mapView !is EditableMapView) {
                        it.source.respond("Cannot edit maps from this command source")
                        return@executes 0
                    }

                    val pos = Vector3IntArgument.getVector3Int(it, "pos")
                    val map = mapView.getActionableMap(pos)
                    if (map == null) {
                        it.source.respond("Actionable map is not loaded")
                        return@executes 0
                    }

                    val chunk = map.dimensions.of(pos)
                    natureGenerator.generate(map, chunk)
                    return@executes 1
                }
            )
        )

        dispatcher.register(
            literal("map").then(
                literal("load").then(
                    argument("name", StringArgumentType.string()).executes {
                        val name = StringArgumentType.getString(it, "name")
                        val map = mapManager.getLoadedMapByName(name) ?: scopedMapManager.getMapByName(name) ?: mapService.loadMapFromDisk(name)
                        it.source.respond("Loading map $name for ${it.source.name}")
                        it.source.mapView.load(map)
                        1
                    }
                )
            ).then(
                literal("unload").then(
                    argument("name", StringArgumentType.string()).executes {
                        val name = StringArgumentType.getString(it, "name")
                        it.source.mapView.unload(name)
                        it.source.respond("Unloaded map $name for ${it.source.name}")
                        1
                    }
                )
            ).then(
                literal("edit").executes {
                    val mapView = it.source.mapView
                    if (mapView !is EditableMapView) {
                        it.source.respond("Cannot edit maps from this command source")
                        return@executes 0
                    }

                    it.source.respond("Currently editing map '${mapView.editingMap}'")
                    1
                }.then(
                    argument("name", StringArgumentType.string()).executes {
                        val name = StringArgumentType.getString(it, "name")
                        val mapView = it.source.mapView
                        if (mapView !is EditableMapView) {
                            it.source.respond("Cannot edit maps from this command source")
                            return@executes 0
                        }

                        mapView.editingMap = name
                        it.source.respond("Now editing map '$name'")
                        1
                    }
                )
            ).then(
                literal("list").executes {
                    val mapView = it.source.mapView
                    val maps = mapView.loadedMaps.map { map -> map.name }
                    if (maps.isEmpty()) {
                        it.source.respond("No maps loaded")
                    } else {
                        it.source.respond("Loaded maps: ${maps.joinToString()}")
                    }
                    maps.size
                }
            ).then(
                literal("get").executes {
                    val entity = (it.source as? EntityCommandSource)?.entity
                    if (entity != null) {
                        val entityId = idMapper[entity]?.id?.toString() ?: "(missing id)"
                        val map = mapReferenceMapper[entity]?.map
                        if (map != null) {
                            it.source.respond("Entity $entityId is attached to map ${map.name}")
                            1
                        } else {
                            it.source.respond("Entity $entityId is not attached to any map")
                            0
                        }
                    } else {
                        it.source.respond("Cannot get attached map from this command source")
                        0
                    }
                }.then(
                    argument("pos", Vector3IntArgument.vector3Int()).executes {
                        val position = Vector3IntArgument.getVector3Int(it, "pos")
                        val mapView = it.source.mapView
                        val map: EiradirMap? = if (mapView is EditableMapView) {
                            mapView.getActionableMap(position)
                        } else {
                            mapManager.getActionableMapFromMerged(position)
                        }
                        if (map != null) {
                            it.source.respond("Actionable map at $position is ${map.name}")
                            1
                        } else {
                            it.source.respond("No actionable map at $position")
                            0
                        }
                    })
            ).then(
                literal("set").then(
                    argument("map", StringArgumentType.string()).executes {
                        val entity = (it.source as? EntityCommandSource)?.entity
                        if (entity != null) {
                            val entityId = idMapper[entity]?.id?.toString() ?: "(missing id)"
                            val mapReference = mapReferenceMapper[entity]
                            if (mapReference != null) {
                                val name = StringArgumentType.getString(it, "map")
                                val map = mapManager.getLoadedMapByName(name) ?: scopedMapManager.getMapByName(name)
                                if (map != null) {
                                    val oldMap = mapReference.map ?: map
                                    mapReference.map = map
                                    eventBus.post(EntitySwitchedMapEvent(entity, oldMap, map))
                                    it.source.respond("Attached entity $entityId to map '${map.name}'")
                                    1
                                } else {
                                    it.source.respond("No map with name '$name'")
                                    0
                                }
                            } else {
                                it.source.respond("Entity $entityId can not be attached to map")
                                0
                            }
                        } else {
                            it.source.respond("Cannot attach this command source to a map")
                            0
                        }
                    }
                )
            )
        )
    }

    private fun getTileAt(source: CommandSource, pos: Vector3Int): Int {
        val tile = source.mapView.getTileAt(pos)
        source.respond("Tile at $pos is $tile")
        return 1
    }

    private fun setTileAt(source: CommandSource, pos: Vector3Int, tile: Tile?): Int {
        val mapView = source.mapView
        if (mapView !is EditableMapView) {
            source.respond("Cannot edit maps from this command source")
            return 0
        }

        val map = mapView.getActionableMap(pos)
        if (map == null) {
            source.respond("Actionable map is not loaded")
            return 0
        }

        map.setTileAt(pos, tile)
        eventBus.post(TileUpdatedEvent(map, map.dimensions, pos, tile))
        source.respond("Tile set to $tile at $pos")
        return 1
    }
}