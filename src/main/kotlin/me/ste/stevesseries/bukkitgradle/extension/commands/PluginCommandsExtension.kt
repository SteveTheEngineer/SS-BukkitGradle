package me.ste.stevesseries.bukkitgradle.extension.commands

import org.gradle.api.provider.MapProperty

abstract class PluginCommandsExtension {
    abstract val commands: MapProperty<String, PluginCommand>

    fun command(command: PluginCommand) {
        if (command.name in this.commands.get()) {
            throw IllegalArgumentException("Duplicate command name: ${command.name}.")
        }

        this.commands.put(command.name, command)
    }

    fun command(
        name: String,
        description: String? = null,
        aliases: Set<String> = emptySet(),
        permission: String? = null,
        permissionMessage: String? = null,
        usage: String? = null
    ) =
        this.command(
            PluginCommand(
                name = name,
                description = description,
                aliases = aliases,
                permission = permission,
                permissionMessage = permissionMessage,
                usage = usage
            )
        )
}