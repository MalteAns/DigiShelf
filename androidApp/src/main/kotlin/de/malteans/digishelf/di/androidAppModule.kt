package de.malteans.digishelf.di

import de.malteans.digishelf.BuildConfig
import de.malteans.digishelf.core.data.network.ApiConfig
import org.koin.core.module.Module
import org.koin.dsl.module

val androidAppModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig(BuildConfig.GOOGLE_API_KEY) }
    }