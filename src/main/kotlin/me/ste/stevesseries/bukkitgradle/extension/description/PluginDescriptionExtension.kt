package me.ste.stevesseries.bukkitgradle.extension.description

import org.gradle.api.Project
import org.gradle.api.internal.provider.DefaultProvider
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.util.concurrent.Callable

abstract class PluginDescriptionExtension(
    project: Project
) {
    abstract val name: Property<String>
    abstract val version: Property<String>
    abstract val mainClass: Property<String>

    abstract val description: Property<String>
    abstract val apiVersion: Property<String>
    abstract val loadingPhase: Property<LoadingPhase>
    abstract val authors: SetProperty<String>
    abstract val website: Property<String>
    abstract val logPrefix: Property<String>
    abstract val mavenCentralLibraries: SetProperty<String>

    abstract val additionalDependencies: SetProperty<String>
    abstract val additionalSoftDependencies: SetProperty<String>
    abstract val additionalLoadBeforeDependencies: SetProperty<String>

    abstract val excludeDependencies: SetProperty<String>
    abstract val excludeSoftDependencies: SetProperty<String>
    abstract val excludeLoadBeforeDependencies: SetProperty<String>

    abstract val overrides: MapProperty<String, Any>

    init {
        this.name.convention(project.name)
        this.version.convention(DefaultProvider { project.version.toString() })

        this.description.convention(DefaultProvider { project.description })
        this.loadingPhase.convention(LoadingPhase.AFTER_WORLD_LOAD)
        this.logPrefix.convention(this.name)
    }

    fun setBasicValues(values: MutableMap<String, Any>) {
        val name = this.name.get()

        // Required properties
        values["main"] = this.mainClass.orNull
            ?: throw IllegalStateException("Missing main class. Please set the mainClass property in the pluginDescription block.")
        values["name"] = name
        values["version"] = this.version.get()

        // Optional properties
        val optionalValues = mutableMapOf<String, Any?>(
            "description" to this.description.orNull,
            "api-version" to this.apiVersion.orNull,
            "website" to this.website.orNull
        )

        val loadingPhase = this.loadingPhase.get()
        if (loadingPhase != LoadingPhase.AFTER_WORLD_LOAD) {
            values["load"] = loadingPhase.bukkitName
        }

        val authors = this.authors.get()
        if (authors.isNotEmpty()) {
            if (authors.size == 1) {
                values["author"] = authors.first()
            } else {
                values["authors"] = authors.toList()
            }
        }

        val logPrefix = this.logPrefix.get()
        if (logPrefix != name) {
            values["prefix"] = logPrefix
        }

        val mavenCentralLibraries = this.mavenCentralLibraries.get()
        if (mavenCentralLibraries.isNotEmpty()) {
            values["libraries"] = mavenCentralLibraries.toList()
        }

        // Apply the properties
        for (optionalValue in optionalValues) {
            val value = optionalValue.value ?: continue
            values[optionalValue.key] = value
        }
    }
}