package com.example.bookshelf

import com.example.bookshelf.model.ImageLinks
import com.example.bookshelf.model.SearchItem
import com.example.bookshelf.model.SearchResponse
import com.example.bookshelf.model.VolumeInfo
import com.example.bookshelf.model.VolumeResponse
import com.example.bookshelf.network.BooksApiService

/** Service fictif : aucune requête réelle vers l'API Google Books. */
class FakeBooksApiService : BooksApiService {

    val volumes = listOf(
        VolumeResponse(
            id = "id1",
            volumeInfo = VolumeInfo(
                title = "Jazz History",
                authors = listOf("A. Author", "B. Writer"),
                imageLinks = ImageLinks(thumbnail = "http://books.example.com/cover1.jpg")
            )
        ),
        VolumeResponse(
            id = "id2",
            volumeInfo = VolumeInfo(
                title = "Blue Notes",
                authors = null,
                imageLinks = null
            )
        )
    )

    override suspend fun searchBooks(query: String, maxResults: Int): SearchResponse =
        SearchResponse(items = volumes.map { SearchItem(it.id) })

    override suspend fun getBook(id: String): VolumeResponse =
        volumes.first { it.id == id }
}
