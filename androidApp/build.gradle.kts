plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "de.malteans.digishelf"
    compileSdk {
        version = release(libs.versions.android.targetSdk.get().toInt())
    }

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.projectVersionCode.get().toInt()
        versionName = libs.versions.projectVersionName.get()
        versionNameSuffix = libs.versions.projectVersionNameSuffix.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.composeApp)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.bundles.filekit)

    debugImplementation(libs.compose.ui.tooling)
}
