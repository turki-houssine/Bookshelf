package com.example.bookshelf.model

/** Réponse de GET /volumes?q=... (on ne garde que l'identifiant de chaque livre). */
data class SearchResponse(
    val items: List<SearchItem>? = null
)

data class SearchItem(
    val id: String
)

/** Réponse de GET /volumes/{id}. Gson peut produire des null : tous les champs optionnels sont nullables. */
data class VolumeResponse(
    val id: String,
    val volumeInfo: VolumeInfo? = null
)

data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val imageLinks: ImageLinks? = null
)

data class ImageLinks(
    val thumbnail: String? = null
)

/** Modèle utilisé par l'interface utilisateur. */
data class Book(
    val id: String,
    val title: String,
    val authors: String,
    val thumbnailUrl: String?
)
