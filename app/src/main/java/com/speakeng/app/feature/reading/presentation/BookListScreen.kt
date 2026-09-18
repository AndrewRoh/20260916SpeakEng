package com.speakeng.app.feature.reading.presentation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.model.BookSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    onBookClick: (String) -> Unit,
    viewModel: BookListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val uploadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.uploadBook(it, resolveTxtFileName(context, it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Books") },
                actions = {
                    IconButton(onClick = { uploadLauncher.launch(arrayOf("text/plain")) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Upload a book")
                    }
                },
            )
        },
    ) { padding ->
        UiStateContent(uiState = uiState, modifier = Modifier.padding(padding)) { books ->
            BookListContent(books = books, onBookClick = onBookClick, onDelete = viewModel::deleteBook)
        }
    }
}

/** Reads the SAF-picked file's display name, falling back to a generated one, and ensures a .txt extension. */
private fun resolveTxtFileName(context: Context, uri: Uri): String {
    val displayName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
    }
    val name = displayName ?: "book_${System.currentTimeMillis()}.txt"
    return if (name.endsWith(".txt", ignoreCase = true)) name else "$name.txt"
}

@Composable
private fun BookListContent(books: List<Book>, onBookClick: (String) -> Unit, onDelete: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(books, key = { it.id }) { book ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onBookClick(book.id) },
                    ) {
                        Text(text = book.title)
                        Text(text = if (book.source == BookSource.BUNDLED) "Sample" else "My upload")
                    }
                    if (book.source == BookSource.UPLOADED) {
                        IconButton(onClick = { onDelete(book.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookListScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                listOf(
                    Book("bundled:sample.txt", "A Walk In The Park", BookSource.BUNDLED, "sample.txt"),
                    Book("uploaded:mybook.txt", "My Book", BookSource.UPLOADED, "/tmp/mybook.txt"),
                ),
            ),
        ) { BookListContent(it, onBookClick = {}, onDelete = {}) }
    }
}
