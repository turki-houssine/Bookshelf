package com.example.bookshelf

import com.example.bookshelf.data.NetworkBooksRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkBooksRepositoryTest {

    private val fakeService = FakeBooksApiService()
    private val repository = NetworkBooksRepository(fakeService)

    @Test
    fun searchBooks_returnsOneBookPerVolume() = runTest {
        val books = repository.searchBooks("jazz")

        assertEquals(listOf("id1", "id2"), books.map { it.id })
        assertEquals(listOf("Jazz History", "Blue Notes"), books.map { it.title })
    }

    @Test
    fun searchBooks_replacesHttpWithHttpsInThumbnailUrl() = runTest {
        val books = repository.searchBooks("jazz")

        assertEquals("https://books.example.com/cover1.jpg", books[0].thumbnailUrl)
    }

    @Test
    fun searchBooks_handlesMissingAuthorsAndImage() = runTest {
        val book = repository.searchBooks("jazz")[1]

        assertEquals("", book.authors)
        assertNull(book.thumbnailUrl)
    }

    @Test
    fun searchBooks_joinsAuthors() = runTest {
        val book = repository.searchBooks("jazz")[0]

        assertEquals("A. Author, B. Writer", book.authors)
    }
}
