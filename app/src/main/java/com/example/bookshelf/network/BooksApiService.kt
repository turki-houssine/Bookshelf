package com.example.bookshelf.network

import com.example.bookshelf.model.SearchResponse
import com.example.bookshelf.model.VolumeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Service Retrofit pour l'API Google Books. */
interface BooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): SearchResponse

    @GET("volumes/{id}")
    suspend fun getBook(@Path("id") id: String): VolumeResponse
}
