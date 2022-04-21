package me.ste.stevesseries.bukkitgradle.extension.description

enum class LoadingPhase(val bukkitName: String) {
    STARTUP("STARTUP"),
    AFTER_WORLD_LOAD("POSTWORLD")
}