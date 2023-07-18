package net.eiradir.server.process.persistence

data class ProcessInstanceData(val id: Int, val contextData: Map<String, Any>, val taskData: Map<String, Any>, val checkpoint: String?)