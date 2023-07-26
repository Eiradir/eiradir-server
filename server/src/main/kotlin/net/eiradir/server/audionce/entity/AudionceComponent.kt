package net.eiradir.server.audionce.entity

import com.badlogic.ashley.core.Component
import net.eiradir.server.audionce.Audionce

class AudionceComponent : Component {
    val audionces = mutableListOf<Audionce>()
}