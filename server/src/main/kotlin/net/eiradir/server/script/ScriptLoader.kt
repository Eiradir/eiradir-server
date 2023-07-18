package net.eiradir.server.script

import net.eiradir.server.extensions.logger
import java.io.File
import java.nio.file.Files
import javax.script.ScriptEngineManager

class ScriptLoader {
    private val log = logger()

    fun load(directory: File) {
        if (!directory.exists()) {
            return
        }

        // setIdeaIoUseFallback()

        val engine = ScriptEngineManager().getEngineByExtension("kts")
        val rootPath = directory.toPath()
        Files.walk(rootPath)
            .filter { it.toString().endsWith(".kts") }
            .forEach { path ->
                log.info("Loading script {}", path)
                path.toFile().bufferedReader().use {
                    engine.eval(it)
                }
            }
    }
}