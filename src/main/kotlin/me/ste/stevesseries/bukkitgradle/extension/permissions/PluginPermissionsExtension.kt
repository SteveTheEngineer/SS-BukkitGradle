package me.ste.stevesseries.bukkitgradle.extension.permissions

import org.gradle.api.provider.MapProperty

abstract class PluginPermissionsExtension {
    abstract val permissions: MapProperty<String, PluginPermission>

    fun permission(permission: PluginPermission) {
        if (permission.node in this.permissions.get()) {
            throw IllegalArgumentException("Duplicate permission node: ${permission.node}.")
        }

        this.permissions.put(permission.node, permission)
    }

    fun permission(
        node: String,
        description: String? = null,
        defaultGrant: PermissionDefaultGrant = PermissionDefaultGrant.OPERATORS,
        children: Map<String, Boolean> = emptyMap()
    ) = this.permission(
        PluginPermission(
            node = node,
            description = description,
            defaultGrant = defaultGrant,
            children = children
        )
    )
}