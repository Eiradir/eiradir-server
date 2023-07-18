package net.eiradir.server

import java.util.concurrent.ConcurrentLinkedQueue

class MainThreadQueue {
    private val tasks = ConcurrentLinkedQueue<() -> Unit>()

    fun scheduleTask(function: () -> Unit) {
        tasks.add(function)
    }

    fun processTasks() {
        var task = tasks.poll()
        while (task != null) {
            try {
                task()
                task = tasks.poll()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}