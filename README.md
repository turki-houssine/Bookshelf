# Bookshelf

Application Android (Kotlin + Jetpack Compose) du projet final du module 5 d'« Android Basics with Compose » :
recherche de livres via l'API Google Books et affichage des couvertures dans une grille adaptative.

## Ouvrir le projet
1. Android Studio → **File > Open** → choisir le dossier `Bookshelf`.
2. Laisser Gradle synchroniser (Android Studio propose de télécharger Gradle 8.7 d'après `gradle-wrapper.properties`).
3. Lancer sur un émulateur ou un téléphone (minSdk 24).
4. Tests : clic droit sur `app/src/test` → **Run Tests**, ou `./gradlew test`.

## Architecture
- `network/BooksApiService` : service Retrofit (`GET volumes?q=`, `GET volumes/{id}`), parsing Gson.
- `data/BooksRepository` : interface + `NetworkBooksRepository` (1 recherche, puis 1 requête par livre, séquentielles dans la même coroutine ; `http` → `https`).
- `data/AppContainer` : injection manuelle de dépendances (`BookshelfApplication`).
- `ui/BookshelfViewModel` : état `Loading / Success / Error`, recherche annulable.
- `ui/BookshelfScreen` : champ de recherche, grille `GridCells.Adaptive(150.dp)`, `AsyncImage` (Coil), chargement / erreur / réessayer.
- Tests : `FakeBooksApiService` (aucune requête réelle), test du dépôt et du ViewModel.
