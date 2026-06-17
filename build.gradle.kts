import io.github.apdevteam.githubPackage
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.slf4j.event.Level

// Mojang ships Java 21 to end users starting in 1.20.5, so mods should target Java 21.
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

plugins {
    idea
//    id("net.neoforged.licenser") version "0.7.2"
    id("net.neoforged.moddev") version "2.0.107"
    alias(libs.plugins.githubPackages)
}

// Mod stuff
val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project

val datagenOutput: String = "src/generated/resources"

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to modVersion,
        "mod_license" to modLicense,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "minecraft_version_range" to "[${libs.versions.minecraft.get()}]",
        "loader_version_range" to "[1,)",
        "neo_version_range" to "[${libs.versions.neoforge.get()},)",
        "tfc_version_range" to "[${libs.versions.tfc.get()},)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

base {
    archivesName.set("$modName-${libs.versions.minecraft.get()}")
    version = modVersion
    group = modGroupId
}

sourceSets {
    main {
        resources {
            srcDir(datagenOutput)
            srcDir(generateModMetadata)
        }
    }
    test {
        resources {
            // Pull down our generated data for tests
            srcDir(datagenOutput)
        }
    }
    create("data")
}

/**
 * Sets up a dependency configuration called 'localRuntime'.
 * This configuration should be used instead of 'runtimeOnly' to declare
 * a dependency that will be present for runtime testing but that is
 * "optional", meaning it will not be pulled by dependents of this mod.
 */
val localRuntime: Configuration by configurations.creating

val dataImplementation = configurations.getByName("dataImplementation")

configurations.runtimeClasspath.configure {
    extendsFrom(localRuntime, dataImplementation)
}

configurations {
    get("dataCompileClasspath").extendsFrom(compileClasspath.get())
    get("dataRuntimeClasspath").extendsFrom(runtimeClasspath.get())
}
println("Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")

neoForge {
    // Specify the version of NeoForge to use.
    version = libs.versions.neoforge.get()
    addModdingDependenciesTo(sourceSets["data"])
    validateAccessTransformers = true

    parchment {
        minecraftVersion = libs.versions.parchmentMinecraft
        mappingsVersion = libs.versions.parchment
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers = project.files('src/main/resources/META-INF/accesstransformer.cfg')

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        // applies to all the run configs above
        configureEach {

            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("neoforge.logging.markers", "REGISTRIES")
            logLevel = Level.DEBUG
            systemProperty("neoforge.enabledGameTestNamespaces", modId)

            // Only JBR allows enhanced class redefinition, so ignore the option for any other JDKs
            jvmArguments.addAll("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-ea")
        }

        register("client") {
            client()
            gameDirectory = file("run/client")
        }
        register("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
        }

        // register("data") {
        //     data()

        //     sourceSet = sourceSets["data"]
        //     programArguments.addAll("--all",
		// 	 "--mod", modId,
		// 	 "--output", file(datagenOutput).absolutePath,
		// 	 "--existing",  file("src/main/resources").absolutePath,
		// 	 "--existing-mod",
		// 	 "tfc")

        //}

    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["data"])
        }
    }

    unitTest {
        enable()
        testedMod = mods[modId]
    }

    ideSyncTask(generateModMetadata)
}
 
repositories {
    mavenCentral()
    mavenLocal()
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/") }
        filter { includeGroup("dev.emi") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.blamejared.com/") }
        filter { includeGroup("mezz.jei") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.k-4u.nl/") }
        filter { includeGroup("mcjty.theoneprobe") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.blamejared.com") }
        filter { includeGroup("vazkii.patchouli") }
    }
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") }
        filter { includeGroup("curse.maven") }
    }

    // If you have mod jar dependencies in ./libs, you can declare them as a repository like so:
    flatDir {
        dir("libs")
    }
}

dependencies {

    // ModernFix - useful at runtime for significant memory savings in TFC in dev (see i.e. wall block shape caches)
    localRuntime(libs.modernfix)

    // TFC!
    implementation(libs.tfc)

    // Patchouli
    compileOnly(libs.patchouli) { artifact { classifier = "api" } }
    runtimeOnly(libs.patchouli)

    // JEI
    compileOnly(libs.bundles.jei.api)
    runtimeOnly(libs.jei)

    compileOnly("org.slf4j:slf4j-api:1.7.36")

    runtimeOnly(libs.cyanide)

}


// Automatically apply a license header when running checkLicense / updateLicense
//license {
//    header("HEADER.txt")
//
//    include("**/*.java")
//}

idea {
    module {
        // IDEA no longer automatically downloads sources/javadoc jars for dependencies,
        // so we need to explicitly enable the behavior.
        isDownloadSources = true
        isDownloadJavadoc = true

        val elements = arrayOf(
            "run", ".gradle", ".idea", "src/generated/resources/.cache"
        ).map { file(it) }
        excludeDirs.addAll(
            elements
        )
    }
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
        }
    }

    named("neoForgeIdeSync") {
        dependsOn(generateModMetadata)
    }
}
