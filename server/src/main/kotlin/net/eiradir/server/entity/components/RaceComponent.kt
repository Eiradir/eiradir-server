package net.eiradir.server.entity.components

import net.eiradir.server.data.Race
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

class RaceComponent(var race: Race) : PersistedComponent {
    override val serializedName = "Race"

    var originalRace: Race = race

    override fun save(buf: SupportedOutput) {
        buf.writeId(race)
        buf.writeId(originalRace)
    }

    override fun load(buf: SupportedInput) {
        race = buf.readFromRegistry { it.races }
        originalRace = buf.readFromRegistry { it.races }
    }
}