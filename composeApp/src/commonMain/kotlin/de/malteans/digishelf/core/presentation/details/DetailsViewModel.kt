package de.malteans.digishelf.core.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModel (
    private val repository: BookRepository
): ViewModel() {

    private val _bookId = MutableStateFlow<Long?>(null)

    private val _bookSeriesList = repository.querySeries()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _book: Flow<Book?> = _bookId
        .flatMapLatest { bookId ->
            if (bookId != null) {
                repository.getBook(bookId)
            } else {
                flowOf(null)
            }
        }

    private val _state = MutableStateFlow(DetailsState())

    val state = combine(_book, _state, _bookSeriesList) { book, state, bookSeriesList ->
        val coverImageChanged = state.imageUrl != book?.imageUrl
        val isbnChanged = state.isbn != book?.isbn
        val ratingChanged = !((state.rating == book?.rating) ||
                (state.rating == 0 && book?.rating == null))
        val tensionLevelChanged = !((state.tensionLevel == book?.tensionLevel) ||
                (state.tensionLevel == 0 && book?.tensionLevel == null))
        val spiceLevelChanged = !((state.spiceLevel == book?.spiceLevel) ||
                (state.spiceLevel == 0 && book?.spiceLevel == null))
        val emotionLevelChanged = !((state.emotionLevel == book?.emotionLevel) ||
                (state.emotionLevel == 0 && book?.emotionLevel == null))
        val chapterLengthChanged = !((state.chapterLength == book?.chapterLength) ||
                (state.chapterLength == 0 && book?.chapterLength == null))
        val endingRatingChanged = !((state.endingRating == book?.endingRating) ||
                (state.endingRating == 0 && book?.endingRating == null))
        val plotRatingChanged = !((state.plotRating == book?.plotRating) ||
                (state.plotRating == 0 && book?.plotRating == null))
        val titleChanged = state.title != book?.title
        val authorChanged = state.author != book?.author
        val priceChanged = state.price != book?.price || state.currency != book?.currency
        val pageCountChanged = state.pageCount != book?.pageCount
        val statusChanged = (state.possessionStatus != book?.possessionStatus)
                || (state.readStatus != book.readStatus) || (state.ebookStatus != book.eBookStatus)
        val readingTimeChanged = state.readingTime != book?.readingTime
        val seriesChanged = (state.series?.id != book?.bookSeries?.id)
        val descriptionChanged = state.description != book?.description

        state.copy(
            bookId = book?.id,
            book = book,

            imageUrlChanged = coverImageChanged,
            isbnChanged = isbnChanged,
            ratingChanged = ratingChanged,
            tensionLevelChanged = tensionLevelChanged,
            spiceLevelChanged = spiceLevelChanged,
            emotionLevelChanged = emotionLevelChanged,
            chapterLengthChanged = chapterLengthChanged,
            endingRatingChanged = endingRatingChanged,
            plotRatingChanged = plotRatingChanged,
            titleChanged = titleChanged,
            authorChanged = authorChanged,
            priceChanged = priceChanged,
            pagesChanged = pageCountChanged,
            descriptionChanged = descriptionChanged,
            statusChanged = statusChanged,
            readingTimeChanged = readingTimeChanged,
            seriesChanged = seriesChanged,
            somethingChanged = coverImageChanged || isbnChanged || ratingChanged || tensionLevelChanged ||
                    spiceLevelChanged || emotionLevelChanged || chapterLengthChanged || endingRatingChanged ||
                    plotRatingChanged || titleChanged ||
                    authorChanged || priceChanged || pageCountChanged || statusChanged ||
                    readingTimeChanged || seriesChanged || descriptionChanged,

            bookSeriesList = bookSeriesList,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsState())

    fun onAction(action: DetailsAction) {
        when (action) {
            is DetailsAction.SetBookId -> {
                _bookId.update {
                    action.bookId
                }
                viewModelScope.launch(Dispatchers.IO) {
                    onBookChanged(
                        book = repository.getBook(action.bookId).first()
                            ?: throw IllegalArgumentException("Invalide book id ${action.bookId} passed to DetailsViewModel")
                    )
                }
            }
            is DetailsAction.TitleChanged -> {
                _state.value = _state.value.copy(title = action.title)
            }

            is DetailsAction.AuthorChanged -> {
                _state.value = _state.value.copy(author = action.author)
            }
            is DetailsAction.IsbnChanged -> {
                val newIsbn = action.isbn
                _state.update {
                    it.copy(isbn = newIsbn)
                }
                //check if isbn is already in the database
                viewModelScope.launch(Dispatchers.IO) {
                    repository.queryBooks(isbnQuery = newIsbn).first() . forEach { bookWithIsbn ->
                        if (bookWithIsbn.id != _state.value.bookId) {
                            _state.update {
                                it.copy(isDoubleIsbn = true)
                            }
                            return@launch
                        }
                    }
                    _state.update {
                        it.copy(isDoubleIsbn = false)
                    }
                }
            }
            is DetailsAction.PageCountChanged -> {
                _state.update { it.copy(
                    pageCount = action.pages
                ) }
            }
            is DetailsAction.PriceChanged -> {
                _state.update { it.copy(
                    price = action.price,
                    currency = action.currency,
                ) }
            }
            is DetailsAction.StatusChanged -> {
                _state.update { it.copy(
                    possessionStatus = action.possessionStatus,
                    readStatus = action.readStatus,
                    ebookStatus = action.ebookStatus,
                ) }
            }
            is DetailsAction.RatingChanged -> {
                _state.value = _state.value.copy(rating = action.rating)
            }
            is DetailsAction.TensionLevelChanged -> {
                _state.value = _state.value.copy(tensionLevel = action.level)
            }
            is DetailsAction.SpiceLevelChanged -> {
                _state.value = _state.value.copy(spiceLevel = action.level)
            }
            is DetailsAction.EmotionLevelChanged -> {
                _state.value = _state.value.copy(emotionLevel = action.level)
            }
            is DetailsAction.ChapterLengthChanged -> {
                _state.value = _state.value.copy(chapterLength = action.level)
            }
            is DetailsAction.EndingRatingChanged -> {
                _state.value = _state.value.copy(endingRating = action.level)
            }
            is DetailsAction.PlotRatingChanged -> {
                _state.value = _state.value.copy(plotRating = action.level)
            }
            is DetailsAction.DescriptionChanged -> {
                _state.value = _state.value.copy(description = action.description)
            }
            is DetailsAction.SeriesChanged -> {
                _state.value = _state.value.copy(series = action.series)
            }
            is DetailsAction.ReadingTimeChanged -> {
                _state.value = _state.value.copy(readingTime = action.readingTime)
            }
            is DetailsAction.ImageUrlChanged -> {
                _state.value = _state.value.copy(imageUrl = action.coverImage)
            }
            is DetailsAction.SetOnlineDescription -> {
                _state.value = _state.value.copy(onlineDescription = action.onlineDescription)
            }
            is DetailsAction.SwitchEditing -> {
                _state.value = _state.value.copy(isEditing = !_state.value.isEditing)
            }
            is DetailsAction.UpdateBook -> {
                val book = state.value.book?.copy(
                    imageUrl = _state.value.imageUrl,
                    isbn = _state.value.isbn,
                    rating = when (_state.value.rating) {
                        0 -> null
                        else -> _state.value.rating
                    },
                    tensionLevel = when (_state.value.tensionLevel) {
                        0 -> null
                        else -> _state.value.tensionLevel
                    },
                    spiceLevel = when (_state.value.spiceLevel) {
                        0 -> null
                        else -> _state.value.spiceLevel
                    },
                    emotionLevel = when (_state.value.emotionLevel) {
                        0 -> null
                        else -> _state.value.emotionLevel
                    },
                    chapterLength = when (_state.value.chapterLength) {
                        0 -> null
                        else -> _state.value.chapterLength
                    },
                    endingRating = when (_state.value.endingRating) {
                        0 -> null
                        else -> _state.value.endingRating
                    },
                    plotRating = when (_state.value.plotRating) {
                        0 -> null
                        else -> _state.value.plotRating
                    },
                    title = _state.value.title,
                    author = _state.value.author,
                    pageCount = _state.value.pageCount,
                    price = _state.value.price,
                    readStatus = _state.value.readStatus,
                    readingTime = _state.value.readingTime,
                    possessionStatus = _state.value.possessionStatus,
                    eBookStatus = _state.value.ebookStatus,
                    bookSeries = _state.value.series,
                    description = _state.value.description,
                ) ?: throw IllegalStateException("No book to update")
                viewModelScope.launch(Dispatchers.IO) {
                    if (book.bookSeries?.id == 0L) {
                        val seriesId = repository.addSeries(book.bookSeries)
                        _state.update { it.copy(
                            series = book.bookSeries.copy(id = seriesId)
                        ) }
                        repository.updateBook(book.copy(
                            bookSeries = book.bookSeries.copy(id = seriesId)
                        ))
                    } else {
                        repository.updateBook(book)
                    }
                }
            }
            is DetailsAction.DeleteBook -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.trashBook(state.value.bookId
                        ?: throw IllegalStateException("No book to delete"))
                }
            }
            is DetailsAction.ResetState -> {
                _state.value = DetailsState()
            }

            else -> TODO("This should not happen")
        }
    }

    private fun onBookChanged(book: Book) {
        _state.update {
            it.copy(
                imageUrl = book.imageUrl,
                isbn = book.isbn,
                rating = book.rating ?: 0,
                tensionLevel = book.tensionLevel ?: 0,
                spiceLevel = book.spiceLevel ?: 0,
                emotionLevel = book.emotionLevel ?: 0,
                chapterLength = book.chapterLength ?: 0,
                endingRating = book.endingRating ?: 0,
                plotRating = book.plotRating ?: 0,
                title = book.title,
                author = book.author,
                pageCount = book.pageCount,
                price = book.price,
                currency = book.currency,
                possessionStatus = book.possessionStatus,
                ebookStatus = book.eBookStatus,
                readStatus = book.readStatus,
                readingTime = book.readingTime,
                series = book.bookSeries,
                description = book.description,
                onlineDescription = book.onlineDescription,
                isEditing = false,
            )
        }
    }
}
