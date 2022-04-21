package me.ste.stevesseries.bukkitgradle

import me.ste.stevesseries.bukkitgradle.extension.RunServerExtension
import me.ste.stevesseries.bukkitgradle.extension.commands.PluginCommandsExtension
import me.ste.stevesseries.bukkitgradle.extension.description.PluginDescriptionExtension
import me.ste.stevesseries.bukkitgradle.extension.permissions.PluginPermissionsExtension
import me.ste.stevesseries.bukkitgradle.task.CopyPluginsToServerTask
import me.ste.stevesseries.bukkitgradle.task.DownloadMinecraftServerTask
import me.ste.stevesseries.bukkitgradle.task.GeneratePluginDescriptionTask
import me.ste.stevesseries.bukkitgradle.task.RunMinecraftServerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer

class BukkitGradle : Plugin<Project> {
    private lateinit var pluginDescription: PluginDescriptionExtension
    private lateinit var pluginCommands: PluginCommandsExtension
    private lateinit var pluginPermissions: PluginPermissionsExtension
    private lateinit var runServerConfig: RunServerExtension

    private lateinit var dependOnly: Configuration
    private lateinit var softDependOnly: Configuration
    private lateinit var loadBeforeOnly: Configuration
    private lateinit var pluginRuntimeOnly: Configuration

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
            throw IllegalStateException("The java plugin is required.")
        }

        this.addConfigurations(project.configurations)
        this.addExtensions(project)
        this.addTasks(project.tasks)
    }

    private fun addTasks(tasks: TaskContainer) {
        val generatePluginDescription = tasks.create("generatePluginDescription", GeneratePluginDescriptionTask::class.java, this.pluginDescription, this.pluginCommands, this.pluginPermissions, this.dependOnly, this.softDependOnly, this.loadBeforeOnly)
        val downloadMinecraftServer = tasks.create("downloadMinecraftServer", DownloadMinecraftServerTask::class.java, this.runServerConfig)
        val runMinecraftServer = tasks.create("runMinecraftServer", RunMinecraftServerTask::class.java, this.runServerConfig, downloadMinecraftServer)
        val copyPluginsToServer = tasks.create("copyPluginsToServer", CopyPluginsToServerTask::class.java, this.runServerConfig, this.pluginRuntimeOnly)

        val processResources = tasks.getByName(JavaPlugin.PROCESS_RESOURCES_TASK_NAME)
        val jar = tasks.getByName(JavaPlugin.JAR_TASK_NAME)

        processResources.finalizedBy(generatePluginDescription.path)
        runMinecraftServer.dependsOn(downloadMinecraftServer.path, copyPluginsToServer.path)
        copyPluginsToServer.dependsOn(jar.path)

        val runGroup = "run"
        copyPluginsToServer.group = runGroup
        runMinecraftServer.group = runGroup
    }
    
    private fun addExtensions(project: Project) {
        this.pluginDescription = project.extensions.create("pluginDescription", PluginDescriptionExtension::class.java, project)
        this.pluginCommands = project.extensions.create("pluginCommands", PluginCommandsExtension::class.java)
        this.pluginPermissions = project.extensions.create("pluginPermissions", PluginPermissionsExtension::class.java)
        this.runServerConfig = project.extensions.create("runServer", RunServerExtension::class.java)
    }

    private fun addConfigurations(configurations: ConfigurationContainer) {
        // Create the configurations
        this.pluginRuntimeOnly = configurations.create("pluginRuntimeOnly")
        this.dependOnly = configurations.create("dependOnly")
        this.softDependOnly = configurations.create("softDependOnly")
        this.loadBeforeOnly = configurations.create("loadBeforeOnly")

        val dependCompileOnly = configurations.create("dependCompileOnly")
        val softDependCompileOnly = configurations.create("softDependCompileOnly")
        val loadBeforeCompileOnly = configurations.create("loadBeforeCompileOnly")

        val dependRuntimeOnly = configurations.create("dependRuntimeOnly")
        val softDependRuntimeOnly = configurations.create("softDependRuntimeOnly")
        val loadBeforeRuntimeOnly = configurations.create("loadBeforeRuntimeOnly")

        val depend = configurations.create("depend")
        val softDepend = configurations.create("softDepend")
        val loadBefore = configurations.create("loadBefore")

        val pluginOnly = configurations.create("pluginOnly")

        // Java plugin configurations
        val runtimeOnly = configurations.getByName(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME)
        val compileOnly = configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)

        runtimeOnly.extendsFrom(this.pluginRuntimeOnly)
        compileOnly.extendsFrom(pluginOnly)

        // (soft) Depend compile only, depend runtime only
        dependRuntimeOnly.extendsFrom(depend)
        dependCompileOnly.extendsFrom(depend)

        softDependRuntimeOnly.extendsFrom(softDepend)
        softDependCompileOnly.extendsFrom(softDepend)

        loadBeforeRuntimeOnly.extendsFrom(loadBefore)
        loadBeforeCompileOnly.extendsFrom(loadBefore)

        // Depend + Soft depend only
        this.dependOnly.extendsFrom(dependCompileOnly, dependRuntimeOnly)
        this.softDependOnly.extendsFrom(softDependCompileOnly, softDependRuntimeOnly)
        this.loadBeforeOnly.extendsFrom(loadBeforeCompileOnly, loadBeforeRuntimeOnly)

        // Plugin runtime only
        this.pluginRuntimeOnly.extendsFrom(pluginOnly)

        // Plugin only
        pluginOnly.extendsFrom(dependCompileOnly, softDependCompileOnly, loadBeforeCompileOnly)
    }
}