package net.eiradir.server.extensions

import com.badlogic.gdx.graphics.Color

fun Color.toIntRGBA(): Int {
    return ((255 * this.r).toInt() shl 24) or ((255 * this.g).toInt() shl 16) or ((255 * this.b).toInt() shl 8) or ((255 * this.a).toInt())
}
