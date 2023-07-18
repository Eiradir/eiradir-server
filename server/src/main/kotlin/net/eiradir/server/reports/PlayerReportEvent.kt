package net.eiradir.server.reports

import com.badlogic.ashley.core.Entity

data class PlayerReportEvent(val connection: Entity, val target: Entity?, val message: String)