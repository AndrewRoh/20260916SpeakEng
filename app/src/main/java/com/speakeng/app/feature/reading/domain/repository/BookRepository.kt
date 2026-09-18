package com.speakeng.app.feature.reading.domain.repository

import android.net.Uri
import com.speakeng.app.feature.reading.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    /** Bundled sample books merged with the user's uploaded books. */
    fun getBooks(): Flow<List<Book>>

    suspend fun getBookContent(bookId: String): Result<String>

    /** Copies the file at [uri] into local storage as [fileName]. */
    suspend fun uploadBook(uri: Uri, fileName: String): Result<Book>

    /** Fails if [bookId] refers to a bundled (non-deletable) book. */
    suspend fun deleteBook(bookId: String): Result<Unit>
}
