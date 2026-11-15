package com.example.bookshelf.data

import com.example.bookshelf.network.BooksApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Conteneur d'injection manuelle de dépendances. */
interface AppContainer {
    val booksRepository: BooksRepository
}

class DefaultAppContainer : AppContainer {

    private val baseUrl = "https://www.googleapis.com/books/v1/"

    // Clé API Google Books (ne la publie pas sur GitHub).
    private val apiKey = com.example.bookshelf.BuildConfig.BOOKS_API_KEY
    // Ajoute automatiquement ?key=... à chaque requête.
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", apiKey)
                .build()
            chain.proceed(original.newBuilder().url(url).build())
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitService: BooksApiService by lazy {
        retrofit.create(BooksApiService::class.java)
    }

    override val booksRepository: BooksRepository by lazy {
        NetworkBooksRepository(retrofitService)
    }
}