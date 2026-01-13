package com.mubashshir.lokalmusic.ui.screens.base

sealed class UiEvent
{
    data class ShowError(val message: String) : UiEvent()
    object Unauthorized : UiEvent()
}

