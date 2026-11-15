package com.example.bookshelf

import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import com.example.bookshelf.ui.BookshelfUiState
import com.example.bookshelf.ui.BookshelfViewModel
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookshelfViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsBooksIntoSuccessState() {
        val books = listOf(Book("1", "Titre", "Auteur", "https://x/y.jpg"))
        val viewModel = BookshelfViewModel(object : BooksRepository {
            override suspend fun searchBooks(query: String) = books
        })

        assertEquals(BookshelfUiState.Success(books), viewModel.uiState.value)
    }

    @Test
    fun search_onNetworkFailure_emitsErrorState() {
        val viewModel = BookshelfViewModel(object : BooksRepository {
            override suspend fun searchBooks(query: String): List<Book> = throw IOException("hors ligne")
        })

        assertTrue(viewModel.uiState.value is BookshelfUiState.Error)
    }

    @Test
    fun search_usesCurrentQuery() {
        val received = mutableListOf<String>()
        val viewModel = BookshelfViewModel(object : BooksRepository {
            override suspend fun searchBooks(query: String): List<Book> {
                received += query
                return emptyList()
            }
        })

        viewModel.onQueryChange("  kotlin  ")
        viewModel.search()

        assertEquals(listOf(BookshelfViewModel.DEFAULT_QUERY, "kotlin"), received)
    }
}
