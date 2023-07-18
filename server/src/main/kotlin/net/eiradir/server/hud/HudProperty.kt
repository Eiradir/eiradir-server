package net.eiradir.server.hud

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

abstract class HudProperty<TProperty : Enum<TProperty>, TValue>(val hud: Hud<TProperty, *>, val key: TProperty, var value: TValue) {
    private var provider: (() -> TValue)? = null
    private var lastValue = value
    private var writable = false
    private var throttle = 0f
    private var throttleLeft = 0f

    fun makeWritable(): HudProperty<TProperty, TValue> {
        writable = true
        return this
    }

    fun from(provider: () -> TValue): HudProperty<TProperty, TValue> {
        this.provider = provider
        return this
    }

    fun throttled(throttle: Float): HudProperty<TProperty, TValue> {
        this.throttle = throttle
        return this
    }

    fun update(deltaTime: Float) {
        if (throttleLeft <= 0) {
            lastValue = value
            value = provider?.invoke() ?: value
            if (hasChanged(value, lastValue)) {
                sendUpdate()
            }
            throttleLeft = throttle
        } else {
            throttleLeft -= deltaTime
        }
    }

    open fun hasChanged(value: TValue, lastValue: TValue): Boolean {
        return value != lastValue
    }

    fun sendUpdate() {
        hud.sendPropertyUpdate(key, this)
    }

    fun updateReceived(buf: SupportedInput) {
        val receivedValue = decode(buf)
        if (writable) {
            value = receivedValue
            lastValue = receivedValue
        }
    }

    abstract fun encode(buf: SupportedOutput)
    abstract fun decode(buf: SupportedInput): TValue
}