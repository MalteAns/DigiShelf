plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.kotlin.serialization)
}

// Fix: Explicitly force the Compose Multiplatform resource generator to use a static package name
compose.resources {
    publicResClass = false
    packageOfResClass = "de.malteans.legal.resources"
    generateResClass = auto
}

kotlin {
    android {
        namespace = "de.malteans.legal"
        compileSdk = 37
        minSdk = 30

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "legalKit"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)

            implementation(libs.bundles.compose)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended) // More Icons

            // Back Handler
            implementation(libs.ui.backhandler)

            // About Libraries
            implementation(libs.aboutlibraries.compose.core)
            implementation(libs.aboutlibraries.compose.m3)

            // WebView
            api("io.github.kevinnzou:compose-webview-multiplatform:2.0.3")
        }

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }

        desktopMain.dependencies {

        }

        iosMain.dependencies {

        }
    }
}