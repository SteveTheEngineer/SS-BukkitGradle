package me.ste.stevesseries.bukkitgradle.task

import me.ste.stevesseries.bukkitgradle.extension.RunServerExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.process.CommandLineArgumentProvider
import javax.inject.Inject

abstract class RunMinecraftServerTask @Inject constructor(
    private val runServerConfig: RunServerExtension,
    private val downloadServer: DownloadMinecraftServerTask
) : JavaExec() {
    init {
        this.setWorkingDir(this.runServerConfig.workingDirectory)
        this.mainClass.set("-jar")
        this.jvmArgumentProviders += CommandLineArgumentProvider { this.runServerConfig.jvmArgs.get() }
        this.argumentProviders += CommandLineArgumentProvider {
            setOf(this.downloadServer.outputs.files.singleFile.toString()) + this.runServerConfig.serverArgs.get()
        }
        this.standardInput = System.`in`
    }
}