package de.malteans.digishelf.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.malteans.digishelf.core.data.database.BookDatabase
import de.malteans.digishelf.core.data.database.DatabaseFactory
import de.malteans.digishelf.core.data.network.HttpClientFactory
import de.malteans.digishelf.core.data.network.KtorRemoteBookDataSource
import de.malteans.digishelf.core.data.network.RemoteBookDataSource
import de.malteans.digishelf.core.data.repository.DefaultBookRepository
import de.malteans.digishelf.core.domain.BookRepository
import de.malteans.digishelf.core.presentation.add.AddViewModel
import de.malteans.digishelf.core.presentation.details.DetailsViewModel
import de.malteans.digishelf.core.presentation.main.MainViewModel
import de.malteans.digishelf.core.presentation.overview.OverviewViewModel
import de.malteans.digishelf.core.presentation.settings.SettingsViewModel
import de.malteans.digishelf.series.presentation.overview.SeriesOverviewViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val module = module {
    includes(platformModule)

    single { HttpClientFactory.create(get()) }

    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<BookDatabase>().bookDao }

    single<RemoteBookDataSource> { KtorRemoteBookDataSource(get()) }

    single<BookRepository> { DefaultBookRepository(get(), get()) }

    viewModelOf(::MainViewModel)
    viewModelOf(::OverviewViewModel)
    viewModelOf(::AddViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SeriesOverviewViewModel)
}
