# Project Submission: Lokal – Music Player Application
---

## 1. Project Overview

**Lokal** is a performance-oriented Android music player designed to demonstrate modern development standards. The application leverages **Jetpack Compose** for a reactive UI and follows **Clean Architecture** principles to ensure a decoupled, testable, and scalable codebase.

Key focus areas for this submission include:

* Efficient data handling via **Paging 3**.
* Reactive state management with **Kotlin Flow**.
* Robust background playback using **Media3 ExoPlayer**.
* Network resilience through custom **OkHttp Interceptors**.

---

## 2. Key Features & Implementation

* **Dynamic Queue Management:** Users can add, remove, and reorder tracks within the playback queue seamlessly.
* **High-Performance Search:** Utilizes Kotlin Flow operators (e.g., `debounce`, `distinctUntilChanged`) to provide instant, resource-efficient search results.
* **Optimized Pagination:** Implemented in `SongScreen` and `SearchScreen` to handle large datasets without compromising performance.
* **Playback Experience:** Integrated Mini Player and Full Player views controlled via a centralized Media Service.
* **Connectivity Awareness:** Real-time network state monitoring via a `ConnectivityObserver` with centralized error handling for API failures.

---

## 3. Technical Stack & Architecture

The project strictly adheres to **MVVM (Model-View-ViewModel)** architecture:

### **Presentation Layer (UI)**

* **Jetpack Compose:** Declarative UI components.
* **Compose Navigation:** Type-safe screen transitions.
* **ViewModels:** Encapsulate UI logic and expose state via `StateFlow`.

### **Data Layer**

* **Repository Pattern:** Abstracts data sources from the UI.
* **Paging 3:** Manages incremental data loading for large song lists.
* **Retrofit & OkHttp:** Handles network requests with a custom `ErrorInterceptor` for exception mapping.

### **Playback Layer**

* **Media3 ExoPlayer:** Industry-standard media engine.
* **Foreground Service:** Ensures uninterrupted playback when the app is backgrounded.

---

## 4. Repository & Deliverables

* **GitHub Repository:** [https://github.com/KhanMubashshirAzeem/Lokal-Music.git](https://github.com/KhanMubashshirAzeem/Lokal-Music.git)
* **Executable APK:** Located at `assets/app-debug.apk`
* **Demo Video:** Found at `assets/app_screen_recording.mp4`

---

## 5. Setup Instructions

1. **Clone:** `git clone https://github.com/KhanMubashshirAzeem/Lokal-Music.git`
2. **Import:** Open the project in the latest version of **Android Studio (Hedgehog or newer)**.
3. **Sync:** Allow Gradle to download dependencies (Media3, Dagger Hilt, Paging 3, etc.).
4. **Deploy:** Run the `app` module on an emulator (API 24+) or a physical device.

---

## 6. Project Structure

```text
Project
│   MainActivity.kt
│   MusicApp.kt
│   
├───data
│   ├───interceptor
│   │       ApiException.kt
│   │       ErrorInterceptor.kt
│   │       
│   ├───model
│   │       Album.kt
│   │       AlbumResponse.kt
│   │       AlbumsApiResponse.kt
│   │       All.kt
│   │       Artists.kt
│   │       ArtistsApiResponse.kt
│   │       Data.kt
│   │       DownloadUrl.kt
│   │       Featured.kt
│   │       ImageXX.kt
│   │       PlaylistResponse.kt
│   │       PlaylistsApiResponse.kt
│   │       Primary.kt
│   │       Results.kt
│   │       SimpleAlbum.kt
│   │       SimpleArtist.kt
│   │       SimplePlaylist.kt
│   │       SongsApiResponce.kt
│   │       
│   ├───paging
│   │       SongPagingSource.kt
│   │       
│   ├───remote
│   │       SongApiService.kt
│   │       
│   └───repository
│           SongRepository.kt
│           SongRepositoryImpl.kt
│           
├───di
│       AudioModule.kt
│       ConnectivityModule.kt
│       NetworkModule.kt
│       RepositoryModule.kt
│       
├───player
│       PlayerController.kt
│       
├───service
│       LokalMusicService.kt
│       
├───ui
│   ├───components
│   │       BottomNavItem.kt
│   │       CustomTopBar.kt
│   │       DetailScreenHeader.kt
│   │       SongList.kt
│   │       UiStateComposable.kt
│   │       
│   ├───navigation
│   │       AppNavigation.kt
│   │       Screen.kt
│   │       
│   ├───screens
│   │   │   FavoritesScreen.kt
│   │   │   PlaylistsScreen.kt
│   │   │   SettingsScreen.kt
│   │   │   
│   │   ├───base
│   │   │       BaseViewModel.kt
│   │   │       UiEvent.kt
│   │   │       
│   │   ├───home
│   │   │   │   HomeScreen.kt
│   │   │   │   HomeViewModel.kt
│   │   │   │   
│   │   │   └───tab_screen
│   │   │       ├───album
│   │   │       │       AlbumDetailScreen.kt
│   │   │       │       AlbumDetailViewModel.kt
│   │   │       │       AlbumsContent.kt
│   │   │       │       AlbumsViewModel.kt
│   │   │       │       
│   │   │       ├───artists
│   │   │       │       ArtistDetailScreen.kt
│   │   │       │       ArtistDetailViewModel.kt
│   │   │       │       artistList.kt
│   │   │       │       ArtistsContent.kt
│   │   │       │       ArtistsViewModel.kt
│   │   │       │       
│   │   │       ├───songs
│   │   │       │       SongScreen.kt
│   │   │       │       SongViewModel.kt
│   │   │       │       
│   │   │       └───suggestion
│   │   │               MostPlayedTab.kt
│   │   │               RecentlyPlayedTab.kt
│   │   │               SuggestedContent.kt
│   │   │               
│   │   ├───main
│   │   │       MainScreen.kt
│   │   │       MainViewModel.kt
│   │   │       
│   │   ├───player
│   │   │       FullPlayerScreen.kt
│   │   │       MiniPlayer.kt
│   │   │       PlayerViewModel.kt
│   │   │       
│   │   └───search
│   │           SearchBar.kt
│   │           SearchScreen.kt
│   │           SearchViewModel.kt
│   │           
│   └───theme
│           Color.kt
│           Demins.kt
│           Theme.kt
│           Type.kt
│           
└───util
        ConnectivityObserver.kt
        NetworkConnectivityObserver.kt
        UiState.kt

```

---

## 7. Assumptions & Trade-offs

* **Memory Management:** To prioritize a lightweight footprint, no local database (Room) was implemented. Playback state and queues are maintained in memory for the duration of the session.
* **Network Priority:** The app is designed as a "network-first" application, assuming an active internet connection for streaming.
* **Design Philosophy:** UI focus was placed on structural clarity, component reusability, and smooth state transitions over complex aesthetic animations.

---