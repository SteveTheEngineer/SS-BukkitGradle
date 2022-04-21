package me.ste.stevesseries.bukkitgradle.extension.permissions

data class PluginPermission(
    val node: String,
    val description: String?,
    val defaultGrant: PermissionDefaultGrant,
    val children: Map<String, Boolean>
) {
    fun setValues(values: MutableMap<String, Any>) {
        if (this.description != null) {
            values["description"] = this.description
        }

        if (this.defaultGrant != PermissionDefaultGrant.OPERATORS) {
            values["default"] = this.defaultGrant.bukkitValue
        }

        if (this.children.isNotEmpty()) {
            values["children"] = this.children
        }
    }
}
