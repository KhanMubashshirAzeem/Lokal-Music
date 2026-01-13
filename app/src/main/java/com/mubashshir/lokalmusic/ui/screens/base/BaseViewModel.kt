package com.mubashshir.lokalmusic.ui.screens.base

import androidx.lifecycle.ViewModel
import com.mubashshir.lokalmusic.data.interceptor.ApiException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.IOException

// Steps for centralized error handling:
// 1. Create BaseViewModel with handleThrowable() and events flow
// 2. Extend all ViewModels from BaseViewModel
// 3. In catch blocks, call handleThrowable(e) and update _uiState
// 4. In Composables, collect events and show snackbar or navigate on errors

open class BaseViewModel : ViewModel()
{

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events

    protected suspend fun handleThrowable(t: Throwable)
    {
        when (t)
        {
            is ApiException ->
            {
                if (t.code == 401)
                {
                    _events.emit(UiEvent.Unauthorized)
                } else
                {
                    _events.emit(UiEvent.ShowError(t.message))
                }
            }

            is IOException  ->
            {
                _events.emit(
                    UiEvent.ShowError("No internet connection")
                )
            }

            else            ->
            {
                _events.emit(
                    UiEvent.ShowError("Something went wrong")
                )
            }
        }
    }
}
