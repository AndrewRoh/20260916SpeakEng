package com.speakeng.app.feature.reading.data.repository

import android.net.Uri
import com.speakeng.app.feature.reading.data.datasource.BundledBookSource
import com.speakeng.app.feature.reading.data.datasource.UploadSource
import com.speakeng.app.feature.reading.domain.model.BookSource
import io.mockk.mockk
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class BookRepositoryImplTest {

    private class FakeBundledBookSource(private val files: Map<String, String>) : BundledBookSource {
        override fun list(): List<String> = files.keys.toList()
        override fun readText(fileName: String): String = files.getValue(fileName)
    }

    private class FakeUploadSource(private val bytes: ByteArray) : UploadSource {
        override fun open(uri: Uri): InputStream = ByteArrayInputStream(bytes)
    }

    @Test
    fun `getBooks merges bundled and uploaded books`(@TempDir tempDir: File) = runTest {
        val repository = BookRepositoryImpl(
            bundledBookSource = FakeBundledBookSource(mapOf("sample.txt" to "Hello.")),
            uploadedBooksDir = tempDir,
            uploadSource = FakeUploadSource("My book.".toByteArray()),
        )

        repository.uploadBook(mockk<Uri>(), "mine.txt")

        val books = repository.getBooks().value
        assertEquals(2, books.size)
        assertTrue(books.any { it.source == BookSource.BUNDLED && it.title == "sample" })
        assertTrue(books.any { it.source == BookSource.UPLOADED && it.title == "mine" })
    }

    @Test
    fun `uploadBook copies the file content into the uploaded books dir`(@TempDir tempDir: File) = runTest {
        val repository = BookRepositoryImpl(
            bundledBookSource = FakeBundledBookSource(emptyMap()),
            uploadedBooksDir = tempDir,
            uploadSource = FakeUploadSource("Uploaded content.".toByteArray()),
        )

        val result = repository.uploadBook(mockk<Uri>(), "mine.txt")

        assertTrue(result.isSuccess)
        val book = result.getOrThrow()
        assertEquals("Uploaded content.", repository.getBookContent(book.id).getOrThrow())
    }

    @Test
    fun `deleteBook removes an uploaded book`(@TempDir tempDir: File) = runTest {
        val repository = BookRepositoryImpl(
            bundledBookSource = FakeBundledBookSource(emptyMap()),
            uploadedBooksDir = tempDir,
            uploadSource = FakeUploadSource("content".toByteArray()),
        )
        val book = repository.uploadBook(mockk<Uri>(), "mine.txt").getOrThrow()

        val result = repository.deleteBook(book.id)

        assertTrue(result.isSuccess)
        assertTrue(repository.getBooks().value.none { it.id == book.id })
        assertFalse(File(tempDir, "mine.txt").exists())
    }

    @Test
    fun `deleteBook fails for a bundled book`(@TempDir tempDir: File) = runTest {
        val repository = BookRepositoryImpl(
            bundledBookSource = FakeBundledBookSource(mapOf("sample.txt" to "Hello.")),
            uploadedBooksDir = tempDir,
            uploadSource = FakeUploadSource(ByteArray(0)),
        )
        val bundledBook = repository.getBooks().value.first { it.source == BookSource.BUNDLED }

        val result = repository.deleteBook(bundledBook.id)

        assertTrue(result.isFailure)
    }
}
