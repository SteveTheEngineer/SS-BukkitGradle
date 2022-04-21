package me.ste.stevesseries.bukkitgradle.extension.permissions

enum class PermissionDefaultGrant(val bukkitValue: Any) {
    EVERYONE(true),
    NO_ONE(false),
    OPERATORS("op"),
    NON_OPERATORS("not op")
}
