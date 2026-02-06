import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "com.stevdza_san"
version = "1.0.4" // Semantic versioning for better compatibility

kotlin {
    jvm("desktop")
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "swipeable"
            isStatic = true
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        // Configure for library distribution, not executable
        browser()
    }

    sourceSets {
        val androidMain by getting {
            dependencies {

            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                // WASM-specific dependencies can be added here if needed
            }
        }
    }
}

android {
    namespace = "com.stevdza_san"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

//publishing {
//    publications {
//        create<MavenPublication>("mavenLocal") {
//            from(components["kotlin"])
//        }
//    }
//}

mavenPublishing {
    coordinates(
        groupId = "com.stevdza-san",
        artifactId = "swipeable-kmp",
        version = version.toString() // Uses the version defined above
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("Swipeable KMP")
        description.set("A highly customizable swipeable component that supports both dismiss and reveal behaviors.")
        inceptionYear.set("2024")
        url.set("https://github.com/stevdza-san/Swipeable-KMP")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        // Specify developers information
        developers {
            developer {
                id.set("stevdza-san")
                name.set("Stefan Jovanovic")
                email.set("stefan.jovanavich@gmail.com")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/stevdza-san/Swipeable-KMP")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral()

    // Enable GPG signing for all publications
    signAllPublications()
}
