package de.malteans.digishelf.di

import de.malteans.digishelf.core.data.database.DatabaseFactory
import de.malteans.digishelf.core.data.network.ApiConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle

actual val platformModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig(
            (NSBundle.mainBundle.objectForInfoDictionaryKey("GOOGLE_API_TOKEN") as? String) ?: ""
        ) }

        single<HttpClientEngine> { Darwin.create() }
        single { DatabaseFactory() }
    }