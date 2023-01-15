# SS-BukkitGradle
An example of a plugin can be found [here](https://github.com/SteveTheEngineer/SS-BukkitGradleBoilerplate). Documentation is coming soon.

# Use Bukkit Gradle in your project
build.gradle.kts:
```kotlin
plugins {
    id("com.github.SteveTheEngineer.SS-BukkitGradle") version "1.4"
}
// ...
```
settings.gradle.kts:
```kotlin
// ...
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
        // ...
    }
}
// ...
```
[![](https://jitpack.io/v/SteveTheEngineer/SS-BukkitGradle.svg)](https://jitpack.io/#SteveTheEngineer/SS-BukkitGradle)

# TODO
* Shadow plugin support
* Testing support
* BungeeCord support as a separate plugin
