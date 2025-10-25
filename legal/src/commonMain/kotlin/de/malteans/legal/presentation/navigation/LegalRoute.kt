package de.malteans.legal.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface LegalRoute {
    @Serializable
    object Imprint : LegalRoute

    @Serializable
    object Eula : LegalRoute

    @Serializable
    object Privacy : LegalRoute

    @Serializable
    object Licenses : LegalRoute
}