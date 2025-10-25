package de.malteans.legal.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.malteans.legal.presentation.navigation.LegalRoute

@Composable
fun LegalsList(
    tileContainerColor: Color,
    navigateToLegalScreen: (LegalRoute) -> Unit,
    infoList: List<LegalListItemInfo> = legalListItemInfos,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = spacedBy(4.dp),
        modifier = modifier,
    ) {
        infoList.forEach { infoData ->
            LegalInfoListItem(
                info = infoData,
                containerColor = tileContainerColor,
                modifier = Modifier
                    .clickable { navigateToLegalScreen(infoData.route) }
                    .fillMaxWidth()
            )
        }
    }
}