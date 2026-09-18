package com.speakeng.app.feature.reading.data.repository

import android.net.Uri
import com.speakeng.app.feature.reading.data.datasource.BundledBookSource
import com.speakeng.app.feature.reading.data.datasource.UploadSource
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.model.BookSource
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class BookRepositoryImpl @Inject constructor(
    private val bundledBookSource: BundledBookSource,
    private val uploadedBooksDir: File,
    private val uploadSource: UploadSource,
) : BookRepository {

    private val _books = MutableStateFlow<List<Book>>(emptyList())

    init {
        refreshBooks()
    }

    override fun getBooks(): StateFlow<List<Book>> = _books.asStateFlow()

    override suspend fun getBookContent(bookId: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val book = findBook(bookId)
            when (book.source) {
                BookSource.BUNDLED -> bundledBookSource.readText(book.filePath)
                BookSource.UPLOADED -> File(book.filePath).readText()
            }
        }
    }

    override suspend fun uploadBook(uri: Uri, fileName: String): Result<Book> = withContext(Dispatchers.IO) {
        runCatching {
            require(fileName.endsWith(".txt", ignoreCase = true)) { "Only .txt files are supported" }

            val targetFile = File(uploadedBooksDir, fileName)
            val input = uploadSource.open(uri) ?: error("Unable to open the selected file")
            input.use { source ->
                targetFile.outputStream().use { output -> source.copyTo(output) }
            }

            if (targetFile.length() > MAX_UPLOAD_BYTES) {
                targetFile.delete()
                error("File is larger than ${MAX_UPLOAD_BYTES / (1024 * 1024)}MB")
            }

            refreshBooks()
            Book(id = uploadedId(fileName), title = titleFromFileName(fileName), source = BookSource.UPLOADED, filePath = targetFile.absolutePath)
        }
    }

    override suspend fun deleteBook(bookId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val book = findBook(bookId)
            require(book.source == BookSource.UPLOADED) { "Bundled books cannot be deleted" }
            require(File(book.filePath).delete()) { "Failed to delete file" }
            refreshBooks()
        }
    }

    private fun findBook(bookId: String): Book =
        _books.value.firstOrNull { it.id == bookId } ?: error("Book not found: $bookId")

    private fun refreshBooks() {
        uploadedBooksDir.mkdirs()
        val bundled = bundledBookSource.list().map { fileName ->
            Book(id = bundledId(fileName), title = titleFromFileName(fileName), source = BookSource.BUNDLED, filePath = fileName)
        }
        val uploaded = uploadedBooksDir.listFiles { file -> file.extension.equals("txt", ignoreCase = true) }
            .orEmpty()
            .map { file ->
                Book(id = uploadedId(file.name), title = titleFromFileName(file.name), source = BookSource.UPLOADED, filePath = file.absolutePath)
            }
        _books.value = bundled + uploaded
    }

    private fun bundledId(fileName: String) = "bundled:$fileName"
    private fun uploadedId(fileName: String) = "uploaded:$fileName"

    private fun titleFromFileName(fileName: String): String =
        fileName.removeSuffix(".txt").replace('_', ' ').replace('-', ' ')

    private companion object {
        const val MAX_UPLOAD_BYTES = 1 * 1024 * 1024L
    }
}
