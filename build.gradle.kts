plugins {
    java
    jacoco
}

group = "se.jeremy"
version = "4.1.6"

repositories {
    mavenLocal()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    mavenCentral()
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        html.required.set(false)
    }
}
