# SS-BukkitGradle
An example of a plugin can be found [here](https://github.com/SteveTheEngineer/SS-BukkitGradleBoilerplate). Documentation is coming soon.

# Use Bukkit Gradle in your project
build.gradle.kts:
```kotlin
plugins {
    id("me.ste.stevesseries.bukkitgradle") version "1.2"
}
// ...
```
settings.gradle.kts:
```kotlin
// ...
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://mvn-public.steenesvc.cf/releases")
        // ...
    }
}
// ...
```
Bukkit Gradle is hosted on Steene Public: https://mvn-public.steenesvc.cf/releases

# TODO
* Shadow plugin support
* Testing support
* BungeeCord support as a separate plugin
