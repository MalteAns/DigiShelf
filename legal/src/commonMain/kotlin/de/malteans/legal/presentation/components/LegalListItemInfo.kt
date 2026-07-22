package de.malteans.legal.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Key
import androidx.compose.ui.graphics.vector.ImageVector
import de.malteans.legal.presentation.navigation.LegalRoute
import de.malteans.legal.resources.*
import org.jetbrains.compose.resources.StringResource

data class LegalListItemInfo(
    val icon: ImageVector,
    val title: StringResource,
    val description: StringResource,
    val route: LegalRoute,
)

val legalListItemInfos = listOf(
    LegalListItemInfo(
        icon = Icons.Outlined.Description,
        title = Res.string.imprint_title,
        description = Res.string.imprint_desc,
        route = LegalRoute.Imprint,
    ),
    LegalListItemInfo(
        icon = Icons.Outlined.AdminPanelSettings,
        title = Res.string.privacy_title,
        description = Res.string.privacy_desc,
        route = LegalRoute.Privacy,
    ),
//    LegalListItemInfo(
//        icon = Icons.Outlined.Balance,
//        title = Res.string.eula_title,
//        description = Res.string.eula_desc,
//        route = LegalRoute.Eula,
//    ),
    LegalListItemInfo(
        icon = Icons.Outlined.Key,
        title = Res.string.license_title,
        description = Res.string.license_desc,
        route = LegalRoute.Licenses,
    ),
)
