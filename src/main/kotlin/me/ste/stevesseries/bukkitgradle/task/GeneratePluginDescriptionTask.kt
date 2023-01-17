package me.ste.stevesseries.bukkitgradle.task

import me.ste.stevesseries.bukkitgradle.PluginUtil
import me.ste.stevesseries.bukkitgradle.extension.commands.PluginCommand
import me.ste.stevesseries.bukkitgradle.extension.commands.PluginCommandsExtension
import me.ste.stevesseries.bukkitgradle.extension.description.PluginDescriptionExtension
import me.ste.stevesseries.bukkitgradle.extension.permissions.PluginPermission
import me.ste.stevesseries.bukkitgradle.extension.permissions.PluginPermissionsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml
import java.util.jar.JarFile
import javax.inject.Inject

abstract class GeneratePluginDescriptionTask @Inject constructor(
    private val description: PluginDescriptionExtension,
    private val commands: PluginCommandsExtension,
    private val permissions: PluginPermissionsExtension,

    private val dependOnly: Configuration,
    private val softDependOnly: Configuration,
    private val loadBeforeOnly: Configuration
) : DefaultTask() {
    companion object {
        const val PLUGIN_DESCRIPTION_FILE_NAME = "plugin.yml"
    }

    @TaskAction
    fun exec() {
        val values = this.getValues()

        // Serialize values to YAML
        val output = Yaml().dump(values)

        // Find the resources output directory
        val javaExtension = this.project.extensions.getByType(JavaPluginExtension::class.java)
        val sourceSet = javaExtension.sourceSets.getByName("main")
        val resourcesDir = sourceSet.output.resourcesDir ?: throw IllegalStateException("Resources output directory missing")
        resourcesDir.mkdirs()

        // Write the file
        val file = resourcesDir.resolve(PLUGIN_DESCRIPTION_FILE_NAME)
        file.writeText(output)
    }

    private fun getValues(): Map<String, Any> {
        val values = mutableMapOf<String, Any>()

        // Generated values
        this.description.setBasicValues(values)
        this.setValuesFrom(values, "commands", this.commands.commands.get().values, PluginCommand::name, PluginCommand::setValues)
        this.setValuesFrom(values, "permissions", this.permissions.permissions.get().values, PluginPermission::node, PluginPermission::setValues)
        this.setDependencies(values)

        // Overrides
        values += this.description.overrides.get()

        return values
    }

    private fun setDependencies(values: MutableMap<String, Any>) {
        // Collect the plugin names
        val depend = PluginUtil.getPlugins(this.logger, this.dependOnly).keys.toMutableSet()
        val softDepend = PluginUtil.getPlugins(this.logger, this.softDependOnly).keys.toMutableSet()
        val loadBefore = PluginUtil.getPlugins(this.logger, this.loadBeforeOnly).keys.toMutableSet()

        // Add additional dependencies
        depend += this.description.additionalDependencies.get()
        softDepend += this.description.additionalSoftDependencies.get()
        loadBefore += this.description.additionalLoadBeforeDependencies.get()

        // Remove excluded dependencies
        depend -= this.description.excludeDependencies.get()
        softDepend -= this.description.excludeSoftDependencies.get()
        loadBefore -= this.description.excludeLoadBeforeDependencies.get()

        // Make sure there are no conflicts
        val unique = mutableSetOf<String>()
        unique += depend

        for (name in softDepend) {
            if (name in unique) {
                throw IllegalStateException("A plugin ($name) cannot be both a soft and a hard dependency.")
            }

            unique += name
        }

        for (name in loadBefore) {
            if (name in unique) {
                throw IllegalStateException("A plugin ($name) cannot be both a load before and a soft/hard dependency")
            }
        }

        // Set the values
        if (depend.isNotEmpty()) {
            values["depend"] = depend.toList()
        }

        if (softDepend.isNotEmpty()) {
            values["softdepend"] = softDepend.toList()
        }

        if (loadBefore.isNotEmpty()) {
            values["loadbefore"] = loadBefore.toList()
        }
    }

    private fun <T> setValuesFrom(values: MutableMap<String, Any>, key: String, set: Iterable<T>, getKey: java.util.function.Function<T, String>, setValues: java.util.function.BiConsumer<T, MutableMap<String, Any>>) {
        val commands = mutableMapOf<String, Map<String, Any>>()

        for (command in set) {
            val name = getKey.apply(command)
            val commandValues = mutableMapOf<String, Any>()

            setValues.accept(command, commandValues)

            commands[name] = commandValues
        }

        if (commands.isNotEmpty()) {
            values[key] = commands
        }
    }
}