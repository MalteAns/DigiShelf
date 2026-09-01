package de.malteans.digishelf.series.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.digishelf.core.presentation.components.CustomTopBar
import de.malteans.digishelf.core.presentation.overview.components.BookItem
import de.malteans.digishelf.theme.containerColor
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.book_series
import digishelf.composeapp.generated.resources.book_series_blue
import digishelf.composeapp.generated.resources.book_series_green
import digishelf.composeapp.generated.resources.book_series_purple
import digishelf.composeapp.generated.resources.book_series_red
import digishelf.composeapp.generated.resources.book_series_yellow
import digishelf.composeapp.generated.resources.series_details_no_books
import digishelf.composeapp.generated.resources.series_details_no_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
                title = { Text(stringResource(Res.string.book_series)) },
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
        val scrollState = rememberScrollState()
        
        Column(
            modifier = Modifier
                .padding(pad)
                .verticalScroll(scrollState)
        ) {
            if (state.series == null) {
                return@Column
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.containerColor,
                        shape = MaterialTheme.shapes.extraLarge,
                    )
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(
                        when (state.series.id.rem(5)) {
                            0L -> Res.drawable.book_series_blue
                            1L -> Res.drawable.book_series_green
                            2L -> Res.drawable.book_series_purple
                            3L -> Res.drawable.book_series_red
                            else -> Res.drawable.book_series_yellow
                        }
                    ),
                    contentDescription = state.series.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .aspectRatio(ratio = 0.65f)
                        .weight(0.3f)
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(0.7f)) {
                    Text(
                        text = state.series.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.series.description.ifBlank { stringResource(Res.string.series_details_no_description) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            val books = state.series.books
            if (books.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = stringResource(Res.string.series_details_no_books),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                return@Column
            } else {
                LazyColumn(
                    verticalArrangement = spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                ) {
                    items(state.series.books) { book ->
                        BookItem(
                            book = book,
                            onClick = { onAction(SeriesDetailsAction.NavigateToBookDetails(book.id)) },
                        )
                    }
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