plugins {
    java
}

group = "se.jeremy"
version = "4.1.5"

repositories {
    mavenLocal()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    mavenCentral()
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
}
