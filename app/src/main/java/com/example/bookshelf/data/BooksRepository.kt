package com.example.bookshelf.data

import com.example.bookshelf.model.Book
import com.example.bookshelf.model.VolumeResponse
import com.example.bookshelf.network.BooksApiService
import kotlinx.coroutines.CancellationException
import java.io.IOException
import retrofit2.HttpException

/** Interface de dépôt : l'UI ne dépend que d'elle, ce qui permet de permuter la source de données. */
interface BooksRepository {
    suspend fun searchBooks(query: String): List<Book>
}

/** Implémentation réelle, qui passe par le service Retrofit. */
class NetworkBooksRepository(
    private val apiService: BooksApiService
) : BooksRepository {

    override suspend fun searchBooks(query: String): List<Book> {
        // 1) Première requête : recherche des livres (on récupère les identifiants).
        val ids = apiService.searchBooks(query, MAX_RESULTS)
            .items
            .orEmpty()
            .map { it.id }

        // 2) Requêtes suivantes : détail de chaque livre, l'une après l'autre dans la même coroutine.
        val books = mutableListOf<Book>()
        for (id in ids) {
            try {
                books += apiService.getBook(id).toBook()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                // Un livre en échec est ignoré, les autres restent affichés.
            } catch (e: HttpException) {
                // Idem pour une erreur HTTP sur un livre isolé.
            }
        }
        return books
    }

    companion object {
        const val MAX_RESULTS = 20
    }
}

/** Convertit la réponse réseau en modèle d'UI, en forçant https pour que Coil puisse charger l'image. */
fun VolumeResponse.toBook(): Book = Book(
    id = id,
    title = volumeInfo?.title.orEmpty(),
    authors = volumeInfo?.authors?.joinToString(", ").orEmpty(),
    thumbnailUrl = volumeInfo?.imageLinks?.thumbnail?.replace("http://", "https://")
)
