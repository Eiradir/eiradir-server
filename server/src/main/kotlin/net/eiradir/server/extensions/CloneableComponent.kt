package net.eiradir.server.extensions

import com.badlogic.ashley.core.Component

interface CloneableComponent {
    fun copy(): Component

}