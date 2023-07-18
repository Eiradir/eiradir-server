package net.eiradir.server.persistence

import com.badlogic.ashley.core.Component
import java.util.UUID

class DatabasePersistenceComponent(var accountId: String = UUID(0, 0).toString(), var charId: Int = 0) : Component