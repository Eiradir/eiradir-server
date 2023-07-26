package net.eiradir.server.process.registry

import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class ProcessType(override val name: String, val process: ProcessDefinition) : RegistryEntry<ProcessType> {

    override fun registry(registries: Registries): Registry<ProcessType> {
        return registries.processes
    }

    companion object {
        val Invalid = ProcessType("invalid", ProcessDefinition("invalid"))
    }
}