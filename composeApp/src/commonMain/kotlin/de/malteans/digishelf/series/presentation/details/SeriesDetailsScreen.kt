package de.malteans.digishelf.series.presentation.details

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SeriesDetailsScreenRoot(
    viewModel: SeriesDetailsViewModel = koinViewModel(),
    seriesId: Long,
    onBack: () -> Unit,
) {
    SeriesDetailsScreen(
        seriesId = seriesId,
        onBack = onBack,
    )
}

@Composable
fun SeriesDetailsScreen(
    seriesId: Long,
    onBack: () -> Unit,
) {

}