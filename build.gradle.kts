plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.18.0"
}

group = "me.ste.stevesseries"
version = "1.1"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("bukkitGradle") {
            id = "me.ste.stevesseries.bukkitgradle"
            displayName = "SS-BukkitGradle"
            description = "A Gradle plugin that facilitates in the development of Bukkit plugins."
            implementationClass = "me.ste.stevesseries.bukkitgradle.BukkitGradle"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.yaml:snakeyaml:1.30")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/SteveTheEngineer/SS-BukkitGradle")
            credentials {
                username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME"))?.toString()
                password = (project.findProperty("gpr.key") ?: System.getenv("TOKEN"))?.toString()
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components.getByName("java"))
        }
    }
}

//pluginBundle {
//    website = "https://github.com/SteveTheEngineer/SS-BukkitGradle"
//    vcsUrl = "https://github.com/SteveTheEngineer/SS-BukkitGradle"
//    tags = listOf("bukkit")
//}
