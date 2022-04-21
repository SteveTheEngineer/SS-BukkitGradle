package me.ste.stevesseries.bukkitgradle

import me.ste.stevesseries.bukkitgradle.task.GeneratePluginDescriptionTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.logging.Logger
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.jar.JarFile

object PluginUtil {
    fun getPlugins(logger: Logger, configuration: Configuration): MutableMap<String, File> {
        val plugins = mutableMapOf<String, File>()
        val files = configuration.resolve()

        for (file in files) {
            if (!file.name.endsWith(".jar")) {
                continue
            }

            try {
                val jar = JarFile(file)
                val entry = jar.getJarEntry(GeneratePluginDescriptionTask.PLUGIN_DESCRIPTION_FILE_NAME)
                    ?: continue // A jar file missing a plugin.yml file must NOT result in an error, as the file might as well be a regular library. Ignore

                val stream = jar.getInputStream(entry)

                val description: Map<String, Any> = Yaml().load(stream)
                val name = description["name"] as? String

                if (name == null || "version" !in description || "main" !in description) {
                    logger.warn("Skipping plugin ${file.path} due to an invalid plugin.yml (missing or invalid name, version or main).")
                    continue
                }

                if (name in plugins) {
                    logger.warn("Duplicate plugin name: $name. Will now replace the plugin")
                }

                plugins[name] = file
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        return plugins
    }
}