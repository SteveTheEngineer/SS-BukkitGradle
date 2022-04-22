package me.ste.stevesseries.bukkitgradle.task

import me.ste.stevesseries.bukkitgradle.extension.RunServerExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.concurrent.Callable
import javax.inject.Inject

abstract class DownloadMinecraftServerTask @Inject constructor(
    private val config: RunServerExtension
) : DefaultTask() {
    private fun getServerFile() = this.project.projectDir
        .resolve(this.config.workingDirectory.get())
        .resolve(this.config.serverFilename.get())

    init {
        inputs.property("minecraftServerDownloadUri", Callable { this.config.downloadUri.orNull.toString() })
        outputs.file(Callable { this.getServerFile() })
    }

    @TaskAction
    fun exec() {
        val downloadUriString = this.config.downloadUri.orNull
            ?: throw IllegalStateException("Server download URI is missing. Please set the downloadUri value inside the runServer block.")

        val downloadUri = URI(downloadUriString)

        val output = FileOutputStream(this.getServerFile())
        val input = downloadUri.toURL().openStream()
        input.copyTo(output)
    }
}