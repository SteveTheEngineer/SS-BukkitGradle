plugins {
    kotlin("jvm") version "1.6.20"
    `java-gradle-plugin`
    `maven-publish`
}

group = "me.ste.stevesseries.bukkitgradle"
version = "1.6"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("bukkitGradle") {
            id = "com.github.SteveTheEngineer.SS-BukkitGradle"
            displayName = "SS-BukkitGradle"
            description = "A Gradle plugin that facilitates in the development of Bukkit plugins."
            implementationClass = "me.ste.stevesseries.bukkitgradle.BukkitGradle"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.yaml:snakeyaml:1.30")
    implementation("com.github.t9t.minecraft-rcon-client:minecraft-rcon-client:1.0.0")
}
