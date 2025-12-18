package de.malteans.digishelf.core.presentation.overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.SortType
import de.malteans.digishelf.core.presentation.components.CustomTopBar
import de.malteans.digishelf.core.presentation.overview.components.*
import digishelf.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OverviewScreenRoot(
    viewModel: OverviewViewModel = koinViewModel(),
    openDrawer: () -> Unit,
    onAddBook: () -> Unit,
    onAddBookWithScanner: () -> Unit,
    onOpenBook: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onAction(OverviewAction.ReloadBooks)
    }

    OverviewScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is OverviewAction.OnOpenDrawer -> openDrawer()
                is OverviewAction.OnAddBook -> onAddBook()
                is OverviewAction.OnAddBookWithScanner -> onAddBookWithScanner()
                is OverviewAction.OnOpenBook -> onOpenBook(action.bookId)

                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverviewScreen(
    state: OverviewState,
    onAction: (OverviewAction) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    var showFilterDialog by remember { mutableStateOf(false) }

    if (showFilterDialog) {
        val tempFilterOptions = FilterOptions(
            possessionStatus = state.possessionStatus,
            readStatus = state.readStatus,
            sortType = state.sortType,
            searchType = state.searchType,
        )

        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onReset = {
                onAction(OverviewAction.ResetFilter)
                showFilterDialog = false
            },
            onPositiveClick = {
                showFilterDialog = false
                onAction(
                    OverviewAction.ChangeFilterList(
                        tempFilterOptions.possessionStatus,
                        tempFilterOptions.readStatus,
                        tempFilterOptions.sortType,
                        tempFilterOptions.searchType
                    )
                )
            },
            onFilterChange = { index, value ->
                when (index) {
                    0 -> tempFilterOptions.possessionStatus = value
                    1 -> tempFilterOptions.readStatus = value
                }
            },
            onTypeChange = { index, value ->
                when (index) {
                    0 -> {
                        tempFilterOptions.sortType = when (value) {
                            0 -> SortType.TITLE
                            1 -> SortType.AUTHOR
                            2 -> SortType.SERIES
                            else -> SortType.TITLE
                        }
                    }
                    1 -> {
                        tempFilterOptions.searchType = when (value) {
                            0 -> SearchType.TITLE
                            1 -> SearchType.AUTHOR
                            2 -> SearchType.ISBN
                            3 -> SearchType.Series
                            4 -> SearchType.SeriesId
                            else -> SearchType.TITLE
                        }
                    }
                }
            },
            filterItemsList = listOf(stringResource(Res.string.owned)+":", stringResource(Res.string.read)+":"),
            typeItemsList = mapOf(
                stringResource(Res.string.sort_by)+":" to listOf(stringResource(Res.string.title), stringResource(Res.string.author), stringResource(Res.string.series)),
                stringResource(Res.string.search_by)+":" to listOf(stringResource(Res.string.title), stringResource(Res.string.author),
                    stringResource(Res.string.isbn), stringResource(Res.string.series), stringResource(Res.string.series_id))
            ),
            filterStates = listOf(state.possessionStatus, state.readStatus),
            typeStates = listOf(
                when(state.sortType) {
                    SortType.TITLE -> stringResource(Res.string.title)
                    SortType.AUTHOR -> stringResource(Res.string.author)
                    SortType.SERIES -> stringResource(Res.string.series)
                },
                when(state.searchType) {
                    SearchType.TITLE -> stringResource(Res.string.title)
                    SearchType.AUTHOR -> stringResource(Res.string.author)
                    SearchType.ISBN -> stringResource(Res.string.isbn)
                    SearchType.Series -> stringResource(Res.string.series)
                    SearchType.SeriesId -> stringResource(Res.string.series_id)
                }
            )
        )
    }

    LaunchedEffect(
        state.searchQuery,
        state.possessionStatus,
        state.readStatus,
        state.sortType,
        state.searchType
    ) {
        lazyListState.scrollToItem(0) // Reset scroll position when filters change
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = {
                    Text(text = stringResource(Res.string.app_name))
                },
                navigationAction = { onAction(OverviewAction.OnOpenDrawer) },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Book",
                        modifier = Modifier
                            .padding(12.dp)
                            .combinedClickable (
                                onClick = { onAction(OverviewAction.OnAddBook) },
                                onLongClick = { onAction(OverviewAction.OnAddBookWithScanner) }
                            )
                    )
                }
            )
        },
    ) { pad ->
        Box (modifier = Modifier.fillMaxSize()) {
            Column (
                modifier = Modifier
                    .padding(pad)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        value = state.searchQuery,
                        onValueChange = { onAction(OverviewAction.SearchQueryChanged(it)) },
                        hint = "${stringResource(Res.string.search)} (${state.searchType.toUiText.asString()})",
                        keyboardType = when (state.searchType) {
                            SearchType.ISBN,
                            SearchType.SeriesId
                                -> KeyboardType.Number
                            else
                                -> KeyboardType.Text
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                    Icon(
                        imageVector = CustomFilterIcon,
                        contentDescription = stringResource(Res.string.filter),
                        modifier = Modifier
                            .size(48.dp)
                            .combinedClickable (
                                onClick = {
                                    showFilterDialog = true
                                },
                                onLongClick = {
                                    onAction(OverviewAction.ResetFilter)
//                                    Toast.makeText( // TODO: Replace Toast with Snackbar
//                                        context,
//                                        context.getString(R.string.filter_reset),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    // Vibrate
//                                    (getSystemService(context, Vibrator::class.java) as Vibrator)
//                                        .vibrate(
//                                            VibrationEffect
//                                                .createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
//                                        )
                                }
                            )
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Loading ----------------------------------------------------------------
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            items (
                                items = state.books,
                                key = { book -> book.id }
                            ) { book ->
                                val indexInList = state.books.indexOf(book)
                                if (state.sortType == SortType.SERIES
                                    && (book.bookSeries?.id != state.books.getOrNull(indexInList - 1)?.bookSeries?.id
                                            || (book.bookSeries == null && indexInList == 0))
                                ) {
                                    Text(
                                        text = book.bookSeries?.title ?: stringResource(Res.string.no_series),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 24.dp,
                                                top = 8.dp,
                                                end = 6.dp,
                                                bottom = 2.dp
                                            )
                                    )
                                }
                                BookItem(
                                    book = book,
                                    onClick = {
                                        onAction(OverviewAction.OnOpenBook(book.id))
                                    },
                                    modifier = Modifier
                                        .padding(vertical = 6.dp)
                                )
                            }
                            item {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(Res.string.shown_books, state.books.size),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(128.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class FilterOptions(
    var possessionStatus: Boolean?,
    var readStatus: Boolean?,
    var sortType: SortType,
    var searchType: SearchType,
)