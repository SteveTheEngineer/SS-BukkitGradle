package me.ste.stevesseries.bukkitgradle.task

import com.github.t9t.minecraftrconclient.RconClient
import me.ste.stevesseries.bukkitgradle.extension.RunServerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.*
import javax.inject.Inject

abstract class ReloadServerPluginsTask @Inject constructor(
    private val config: RunServerExtension
) : DefaultTask() {
    private fun getServerPropertiesFile() = this.project.projectDir
        .resolve(this.config.workingDirectory.get())
        .resolve("server.properties")

    @TaskAction
    fun exec() {
        val file = this.getServerPropertiesFile()

        if (!file.isFile) {
            throw IllegalStateException("The server.properties file does not exist. The server must be run at least once before running this task.")
        }

        val stream = file.inputStream()

        // Load the properties
        val properties = Properties()
        properties.load(stream)

        // Get the properties
        val rconEnabled = properties.getProperty("enable-rcon").toBoolean()
        val rconPassword = properties.getProperty("rcon.password", "")
        val rconPort = properties.getProperty("rcon.port", "25575").toInt()

        if (!rconEnabled) {
            throw IllegalStateException("RCON is not enabled in the server configuration. Please set \"enable-rcon=true\" in the server.properties file.")
        }

        if (rconPassword.isEmpty()) {
            throw IllegalStateException("RCON password is not set! Please set the value of rcon.password in the server.properties file.")
        }

        // Connect to RCON
        RconClient.open(this.config.serverHost.get(), rconPort, rconPassword).use {
            it.sendCommand(this.config.reloadCommand.get())
        }
    }
}