import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("fabric-loom") version "1.4-SNAPSHOT"
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

version = property("mod_version")!!
group = property("maven_group")!!

val externalImplementation = configurations.create("externalImplementation") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

base {
    archivesName = project.archivesName.get()
}

configurations.all {
    exclude(group = "com.googlecode.soundlibs", module = "tritonus-share")
}


loom {
    this.splitEnvironmentSourceSets()

    mods {
        this.create("coopersmpwinterclient") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.properties["fabric_kotlin_version"]}")
    modImplementation("com.ptsmods:devlogin:3.4.1")

    modImplementation("net.kyori:adventure-platform-fabric:5.10.0") { include(this) }

    implementation("io.github.llamalad7:mixinextras-fabric:0.3.2") { annotationProcessor(this) { include(this) } }
    modApi("me.shedaniel.cloth:cloth-config-fabric:12.0.119")

    implementation("com.github.goxr3plus:java-stream-player:10.0.2") {
        exclude(group = "maven:commons-io", module = "commons-io")
        implementation("commons-io:commons-io:2.15.1")
        externalImplementation(this)
    }
    implementation("com.github.goxr3plus:jaudiotagger:2.2.7") { include(this) }
    implementation("com.mpatric:mp3agic:0.9.1") { include(this) }
    implementation("com.googlecode.soundlibs:mp3spi:1.9.5-1") { include(this) }
    implementation("org.jflac:jflac-codec:1.5.2") { include(this) }
    implementation("com.github.trilarion:vorbis-support:1.1.0") { include(this) }
    implementation("com.googlecode.soundlibs:tritonus-all:0.3.7.2") { include(this) }
}

repositories {
    mavenCentral()
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io/")
    }
    maven {
        name = "sonatype-oss-snapshots1"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    maven { url = uri("https://maven.shedaniel.me/") }
    maven { url = uri("https://maven.terraformersmc.com/releases/") }
    maven {
        url = uri("https://mcef-download.cinemamod.com/repositories/releases")
    }}

tasks {
    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
        manifest {
            this.attributes["Class-Path"] =
                configurations.getByName("externalImplementation").files.map { "dependencies/${it.name}" }.joinToString(" ")
        }
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

}

val dependsDir = "build/libs/dependencies"
tasks.register("copyDependencies") {
    this.doLast {
        configurations.getByName("externalImplementation").files.forEach { file ->
            copy {
                from(file)
                into(dependsDir)
            }
        }
    }
}

tasks.withType<JavaCompile> {
    this.options.release.set(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


tasks.named("remapJar") {
    this.dependsOn(tasks.named("copyDependencies"))
}
