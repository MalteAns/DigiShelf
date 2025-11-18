package de.malteans.digishelf.series.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.digishelf.core.presentation.components.CustomTopBar
import de.malteans.digishelf.core.presentation.overview.components.BookItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SeriesDetailsScreenRoot(
    viewModel: SeriesDetailsViewModel = koinViewModel(),
    seriesId: Long,
    onBack: () -> Unit,
    onShowBookDetails: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit, seriesId) {
        viewModel.onAction(SeriesDetailsAction.SetSeriesId(seriesId))
    }

    SeriesDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SeriesDetailsAction.NavigateBack -> onBack()
                is SeriesDetailsAction.NavigateToBookDetails -> onShowBookDetails(action.bookId)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun SeriesDetailsScreen(
    state: SeriesDetailsState,
    onAction: (SeriesDetailsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopBar(
                title = { Text(text = state.series?.title ?: "") },
                navigationIcon = {
                    IconButton({ onAction(SeriesDetailsAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
        ) {

            LazyColumn {
                items(state.series?.books ?: emptyList()) { book ->
                    BookItem(
                        book = book,
                        onClick = { onAction(SeriesDetailsAction.NavigateToBookDetails(book.id)) },
                    )
                }
            }
        }
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}