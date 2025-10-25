package de.malteans.digishelf.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object NavBooks : Route
    @Serializable
    sealed interface Books : Route {
        @Serializable
        data object Overview : Books
        @Serializable
        data class Add (
            val isbn: String? = null
        ) : Books
        @Serializable
        data class Details (
            val bookId: Long
        ) : Books
        @Serializable
        object Scanner : Books
    }
    @Serializable
    data object NavSeries : Route
    @Serializable
    sealed interface Series : Route {
        @Serializable
        data object Overview : Series
        @Serializable
        data object Add : Series
        @Serializable
        data class Details (
            val seriesId: Long
        ) : Series
    }
    @Serializable
    data object NavSettings
    @Serializable
    sealed interface Settings : Route {
        @Serializable
        data object Overview : Settings
        @Serializable
        data object Trash
    }
}