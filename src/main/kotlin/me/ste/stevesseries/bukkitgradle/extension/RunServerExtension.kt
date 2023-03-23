package me.ste.stevesseries.bukkitgradle.extension

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class RunServerExtension {
    abstract val workingDirectory: Property<String>
    abstract val serverFilename: Property<String>
    abstract val downloadUri: Property<String>
    abstract val jvmArgs: ListProperty<String>
    abstract val serverArgs: ListProperty<String>
    abstract val reloadCommand: Property<String>
    abstract val serverHost: Property<String>

    init {
        this.workingDirectory.convention("run")
        this.serverFilename.convention("server.jar")
        this.reloadCommand.convention("reload confirm")
        this.serverHost.convention("localhost")
        this.serverArgs.convention(listOf("nogui"))
    }
}
