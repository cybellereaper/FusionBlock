plugins {
    java
}

group = "com.github.cybellereaper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.7")
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")

    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("org.apache.commons:commons-lang3:3.17.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.test {
    useJUnitPlatform()
}
