package de.malteans.digishelf.core.data.network

import de.malteans.digishelf.BuildConfig

actual object ApiConfig {
    actual val googleApiToken: String
        get() = BuildConfig.GOOGLE_API_KEY
}