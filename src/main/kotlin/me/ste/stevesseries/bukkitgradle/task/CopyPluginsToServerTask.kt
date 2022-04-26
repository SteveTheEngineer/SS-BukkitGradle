package me.ste.stevesseries.bukkitgradle.task

import me.ste.stevesseries.bukkitgradle.PluginUtil
import me.ste.stevesseries.bukkitgradle.extension.RunServerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileFilter
import javax.inject.Inject

abstract class CopyPluginsToServerTask @Inject constructor(
    private val runServerConfig: RunServerExtension,
    private val pluginRuntimeOnly: Configuration
) : DefaultTask() {
    companion object {
        const val PLUGINS_DIRECTORY = "plugins"
    }

    @TaskAction
    fun exec() {
        val workDir = this.project.projectDir.resolve(this.runServerConfig.workingDirectory.get())

        // Get and clear the plugin directory
        val pluginsDir = workDir.resolve(PLUGINS_DIRECTORY)
        pluginsDir.mkdirs()

        for (file in pluginsDir.listFiles(FileFilter { it.extension == "jar" })!!) {
            file.delete()
        }

        // Copy project jar
        val projectPluginJar = this.getProjectPluginJar()
        projectPluginJar.copyTo(pluginsDir.resolve(projectPluginJar.name))

        // Copy dependency jars
        val dependencyJars = this.getDependencyJars()
        for (file in dependencyJars) {
            file.copyTo(pluginsDir.resolve(file.name))
        }
    }

    private fun getDependencyJars(): Iterable<File> {
        return PluginUtil.getPlugins(this.logger, this.pluginRuntimeOnly, true).values
    }

    private fun getProjectPluginJar(): File {
        val jar = this.project.tasks.getByName(JavaPlugin.JAR_TASK_NAME)
        return jar.outputs.files.singleFile
    }
}