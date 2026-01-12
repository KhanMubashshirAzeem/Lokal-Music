package com.mubashshir.lokalmusic.util

/**
 * Generic UiState sealed interface for all screens.
 * Each screen should define its own Success data type.
 */
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}