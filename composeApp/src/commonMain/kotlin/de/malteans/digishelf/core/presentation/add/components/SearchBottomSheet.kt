package de.malteans.digishelf.core.presentation.add.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    isSearching: Boolean,
    searchResults: List<Book>,
    onResultSelected: (Book) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Loading state || Empty results
    if (isSearching || searchResults.isEmpty()) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                isSearching -> CircularProgressIndicator()
                searchResults.isEmpty() -> Text(
                    text = "No results found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        return
    }
    
    // Results list
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(searchResults) { book ->
            SearchResultItem(
                book = book,
                onClick = { onResultSelected(book) }
            )
        }
    }
}

@Composable
fun SearchResultItem(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Author
            Text(
                text = "by ${book.author}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // ISBN
            Text(
                text = "ISBN: ${book.isbn}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
