## Generate License Information File:
```shell
  ..\gradlew :composeApp:exportLibraryDefinitions
```

## NavGraph:
```kotlin
navigation<Route.LegalNav>(
    startDestination = LegalRoute.Imprint,
    enterTransition = { slideInHorizontally { it } },
    popExitTransition = { slideOutHorizontally { it } },
) {
    composable<LegalRoute.Imprint> {
        ImprintScreen(
            navigateBack = { navController.popBackStack() },
        )
    }
    composable<LegalRoute.Privacy> {
        var htmlData by remember { mutableStateOf<String?>(null) }
        LaunchedEffect(Unit) {
            htmlData = Res.readBytes("files/privacy_policy_de.html").decodeToString()
        }
        PrivacyScreen(
            htmlData = htmlData,
            navigateBack = { navController.popBackStack() },
        )
    }
    composable<LegalRoute.Licenses> {
        val libraries by produceLibraries {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }
        LicensesScreen(
            libraries = libraries,
            navigateBack = { navController.popBackStack() },
        )
    }
}
```

## Dependencies:
### VersionCatalog:
```toml
agp = "8.12.3"
kotlin = "2.2.20"
compose-multiplatform = "1.9.0"
androidx-activity = "1.11.0"

kotlinStdlib = "2.2.20"

aboutLibraries = "13.1.0"

[libraries]
androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "androidx-activity" }

ui-backhandler = { module = "org.jetbrains.compose.ui:ui-backhandler", version.ref= "compose-multiplatform" }

kotlin-stdlib = { group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version.ref = "kotlinStdlib" }
aboutlibraries-compose-core = { module = "com.mikepenz:aboutlibraries-compose-core", version.ref = "aboutLibraries" }
aboutlibraries-compose-m3 = { module = "com.mikepenz:aboutlibraries-compose-m3", version.ref = "aboutLibraries" }

[plugins]
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
android-kotlin-multiplatform-library = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
compose-multiplatform = { id = "org.jetbrains.compose", version.ref = "compose-multiplatform" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }

jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }

aboutLibraries = { id = "com.mikepenz.aboutlibraries.plugin", version.ref = "aboutLibraries" }
```

### root build.gradle.kts
```kts
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false

    alias(libs.plugins.jetbrains.kotlin.serialization) apply false

    alias(libs.plugins.aboutLibraries) apply false
}
```

### app build.gradle.kts
```kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.jetbrains.kotlin.serialization)

    alias(libs.plugins.aboutLibraries)
}

aboutLibraries {
    export {
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
}

kotlin {
    [...]
    sourceSets {
        [...]
        commonMain.dependencies {
            [...]
            implementation(projects.legal)
            [...]
            // About Libraries
            implementation(libs.aboutlibraries.compose.m3)
        }
    }
}
```