package de.malteans.digishelf.series.presentation.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.presentation.components.CustomDialog
import de.malteans.digishelf.core.presentation.components.CustomTopBar
import de.malteans.digishelf.series.presentation.overview.components.BookSeriesItem
import de.malteans.digishelf.series.presentation.overview.components.SeriesDialog
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.book_series
import digishelf.composeapp.generated.resources.no_series
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SeriesOverviewScreenRoot(
    viewModel: SeriesOverviewViewModel = koinViewModel(),
    openDrawer: () -> Unit,
    navigateToSeriesDetails: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SeriesOverviewScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SeriesOverviewAction.OnOpenDrawer -> openDrawer()
                is SeriesOverviewAction.OnShowSeriesDetails -> navigateToSeriesDetails(action.seriesId)
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun SeriesOverviewScreen(
    state: SeriesOverviewState,
    onAction: (SeriesOverviewAction) -> Unit,
) {

    var showSeriesDialog by remember { mutableStateOf(false) }
    var seriesToEdit by remember { mutableStateOf<BookSeries?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showSeriesDialog) {
        val invalidNames = state.seriesList.map { it.title }.toMutableList()
        invalidNames.remove(seriesToEdit?.title)
        invalidNames.add(stringResource(Res.string.no_series))
        SeriesDialog(
            onDismiss = {
                showSeriesDialog = false
                seriesToEdit = null
            },
            onAdd = { series ->
                onAction(SeriesOverviewAction.SubmitSeries(series))
                showSeriesDialog = false
                seriesToEdit = null
            },
            seriesToEdit = seriesToEdit,
            invalidNames = invalidNames.toList(),
        )
    }

    if (showDeleteDialog) {
        CustomDialog(
            onDismissRequest = {
                showDeleteDialog = false
                seriesToEdit = null
            },
            title = { Text(text = "Serie löschen?") },
            leftIcons = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .clickable {
                            showDeleteDialog = false
                            seriesToEdit = null
                        }
                )
            },
            rightIcons = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable {
                            seriesToEdit?.let { series ->
                                onAction(SeriesOverviewAction.DeleteSeries(series))
                            }
                            showDeleteDialog = false
                            seriesToEdit = null
                        }
                )
            }
        ) {

        }
    }

    Scaffold (
        topBar = {
            CustomTopBar(
                navigationAction = { onAction(SeriesOverviewAction.OnOpenDrawer) },
                title = {
                    Text(
                        text = stringResource(Res.string.book_series),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton({ showSeriesDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Series",
                        )
                    }
                },
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) { pad ->
        Box (
            modifier = Modifier
                .padding(pad)
        ) {
            LazyColumn {
                items(state.seriesList) { series ->
                    BookSeriesItem(
                        series = series,
                        onClick = { onAction(SeriesOverviewAction.OnShowSeriesDetails(series.id)) },
                        onLongClick = {
                            seriesToEdit = series
                            showDeleteDialog = true
                        },
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}