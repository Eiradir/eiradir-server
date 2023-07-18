package net.eiradir.server.registry

import com.google.common.collect.ArrayListMultimap

abstract class Registry<T : RegistryEntry<*>>(val name: String, private val idResolver: IdResolver) {
    private val entriesById = mutableMapOf<Int, T>()
    private val entriesByName = mutableMapOf<String, T>()
    private val entriesByTag = ArrayListMultimap.create<String, T>()

    fun register(entry: T): T {
        if (entriesByName.containsKey(entry.name)) {
            throw IllegalArgumentException("Entry with name ${entry.name} already registered in $this")
        }

        entriesByName[entry.name] = entry
        entriesById[getId(entry) ?: throw IllegalArgumentException("Unresolvable id for entry $entry")] = entry
        if(entry is TaggableRegistryEntry) {
            entry.tags.forEach { tag ->
                entriesByTag.put(tag, entry)
            }
        }
        return entry
    }

    fun getId(entry: T): Int? {
        return idResolver.resolve(name, entry.name)
    }

    fun getId(entryName: String): Int? {
        return idResolver.resolve(name, entryName)
    }

    fun getAll(): Collection<T> {
        return entriesById.values
    }

    fun getByName(name: String): T? {
        return entriesByName[name]
    }

    fun getByTag(tag: String): Collection<T> {
        return entriesByTag[tag]
    }

    fun getById(id: Int): T? {
        return entriesById[id]
    }

    abstract fun invalid(name: String): T
}