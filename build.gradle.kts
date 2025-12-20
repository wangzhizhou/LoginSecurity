import org.yaml.snakeyaml.Yaml

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.yaml:snakeyaml:2.4")
    }
}

val pluginYaml = Yaml().load(File("src/main/resources/plugin.yml").inputStream()) as Map<String, Any>
val apiVersion = pluginYaml["api-version"].toString()
val paperApiVersion = providers.gradleProperty("paperApiVersion").orElse(apiVersion).get()
plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
    id("xyz.jpenilla.run-paper") version "3.0.0"
}

group = "com.lenis0012.bukkit"
version = "3.3.2-SNAPSHOT"
description = "LoginSecurity"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

repositories {
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    mavenCentral()
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    // Lenis utils
    implementation("com.lenis0012.pluginutils:lenisutils-module-system:2.4.1")
    implementation("com.lenis0012.pluginutils:lenisutils-config:2.4.1")
    implementation("com.lenis0012.pluginutils:lenisutils-command:2.4.1")
    implementation("com.lenis0012.pluginutils:lenisutils-updater-api:2.4.1")

    // PaperLib
    implementation("io.papermc:paperlib:1.0.8")

    // SQL libs (provided at runtime by server)
    compileOnly("org.xerial:sqlite-jdbc:3.41.2.2")

    // Provided libraries
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.25.3")

    // Metrics
    implementation("org.bstats:bstats-bukkit:3.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testCompileOnly("io.papermc.paper:paper-api:${paperApiVersion}-R0.1-SNAPSHOT")
    testRuntimeOnly("io.papermc.paper:paper-api:${paperApiVersion}-R0.1-SNAPSHOT")
    testCompileOnly("org.apache.logging.log4j:log4j-core:2.25.3")
    testRuntimeOnly("org.apache.logging.log4j:log4j-core:2.25.3")
    testImplementation("com.google.code.gson:gson:2.10.1")
    testImplementation("com.google.guava:guava:32.1.3-jre")
    testImplementation("org.apache.logging.log4j:log4j-api:2.25.3")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("net.bytebuddy:byte-buddy:1.14.6")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.14.6")
}

val manifestUrl: String = (project.findProperty("updater.manifestUrl") as String?)
    ?: "https://raw.githubusercontent.com/lenis0012/LoginSecurity/master/version_manifest.json"

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            mapOf(
                "project" to mapOf(
                    "name" to project.name,
                    "version" to project.version.toString()
                ),
                "updater" to mapOf(
                    "manifestUrl" to manifestUrl
                )
            )
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("com.lenis0012.pluginutils", "com.lenis0012.bukkit.loginsecurity.libs.pluginutils")
    relocate("com.lenis0012.updater", "com.lenis0012.bukkit.loginsecurity.libs.updater")
    relocate("org.bstats", "com.lenis0012.bukkit.loginsecurity.libs.bstats")
    relocate("io.papermc.lib", "com.lenis0012.bukkit.loginsecurity.libs.paper")
}

tasks.jar {
}

tasks.assemble {
}

val isSnapshot = version.toString().endsWith("-SNAPSHOT")

hangarPublish {
    publications.register(project.name) {
        version = project.version.toString()
        id = project.name
        channel = if (isSnapshot) "Snapshot" else "Release"
        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf(apiVersion)
                dependencies {
                    hangar("Vault") { required = false }
                    hangar("ProtocolLib") { required = false }
                }
            }
        }
    }
}

tasks.runServer {
    minecraftVersion(providers.gradleProperty("paperRunVersion").orElse("1.21.10").get())
    jvmArgs(listOf("-Xms512M", "-Xmx1G", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"))
}
