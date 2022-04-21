package me.ste.stevesseries.bukkitgradle.extension.commands

data class PluginCommand(
    val name: String,
    val description: String?,
    val aliases: Set<String>,
    val permission: String?,
    val permissionMessage: String?,
    val usage: String?
) {
    fun setValues(values: MutableMap<String, Any>) {
        if (this.description != null) {
            values["description"] = description
        }

        if (this.aliases.isNotEmpty()) {
            if (this.aliases.size == 1) {
                values["aliases"] = this.aliases.first()
            } else {
                values["aliases"] = this.aliases.toList()
            }
        }

        if (this.permission != null) {
            values["permission"] = this.permission
        }

        if (this.permissionMessage != null) {
            values["permission-message"] = this.permissionMessage
        }

        if (this.usage != null) {
            values["usage"] = this.usage
        }
    }
}
