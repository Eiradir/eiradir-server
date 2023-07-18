package net.eiradir.server.registry

interface RegistryEntry<T : RegistryEntry<T>> {
    val name: String

    fun registry(registries: Registries): Registry<T>
    fun id(registries: Registries): Int? {
        @Suppress("UNCHECKED_CAST")
        return registry(registries).getId(this as T)
    }
}