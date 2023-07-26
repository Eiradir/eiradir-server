package net.eiradir.server.audionce

import com.badlogic.ashley.core.Entity
import net.eiradir.server.camera.entity.CameraComponent
import net.eiradir.server.chat.ChatMessageType
import net.eiradir.server.math.Vector3Int

class Audionce {
    var position: Vector3Int = Vector3Int.Zero
    var range = 20f
    var lastPosition: Vector3Int = Vector3Int(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
    var followEntity: Entity? = null
    var followCamera: CameraComponent? = null

    var chatHandler: ((Entity, String, ChatMessageType) -> Unit) = { _, _, _ -> }

    fun canHear(position: Vector3Int, multiplier: Float = 1f): Boolean {
        val distance = position.dst(this.position)
        return distance <= range * multiplier
    }
}