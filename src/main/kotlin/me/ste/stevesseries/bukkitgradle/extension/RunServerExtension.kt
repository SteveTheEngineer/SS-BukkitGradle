package me.ste.stevesseries.bukkitgradle.extension

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.io.File

abstract class RunServerExtension {
    abstract val workingDirectory: Property<String>
    abstract val serverFilename: Property<String>
    abstract val downloadUri: Property<String>
    abstract val jvmArgs: ListProperty<String>
    abstract val serverArgs: ListProperty<String>

    init {
        this.workingDirectory.convention("run")
        this.serverFilename.convention("server.jar")
    }
}
