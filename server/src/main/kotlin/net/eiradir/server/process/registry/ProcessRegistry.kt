package net.eiradir.server.process.registry

import net.eiradir.server.process.data.ProcessDefinition
import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class ProcessRegistry(idResolver: IdResolver): Registry<ProcessType>("processes", idResolver) {
    override fun invalid(name: String): ProcessType {
        return ProcessType(name, ProcessDefinition("invalid"))
    }
}