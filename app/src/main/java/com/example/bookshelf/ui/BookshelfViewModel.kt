package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

sealed interface BookshelfUiState {
    data class Success(val books: List<Book>) : BookshelfUiState
    data object Error : BookshelfUiState
    data object Loading : BookshelfUiState
}

class BookshelfViewModel(
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow(DEFAULT_QUERY)
    val query: StateFlow<String> = _query.asStateFlow()

    private var searchJob: Job? = null

    init {
        search()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    /** Lance (ou relance) la recherche pour le texte courant ; annule la recherche précédente. */
    fun search() {
        val term = _query.value.trim().ifEmpty { DEFAULT_QUERY }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = BookshelfUiState.Loading
            _uiState.value = try {
                BookshelfUiState.Success(booksRepository.searchBooks(term))
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                BookshelfUiState.Error
            } catch (e: HttpException) {
                BookshelfUiState.Error
            }
        }
    }

    companion object {
        const val DEFAULT_QUERY = "jazz history"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as BookshelfApplication
                return BookshelfViewModel(application.container.booksRepository) as T
            }
        }
    }
}
