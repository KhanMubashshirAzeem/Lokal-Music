// MainActivity.kt (updated)
package com.mubashshir.lokalmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import com.mubashshir.lokalmusic.ui.screens.main.MainScreen
import com.mubashshir.lokalmusic.ui.theme.LokalMusicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LokalMusicTheme {
                Box(
                    modifier = Modifier.systemBarsPadding()
                ) {
                    MainScreen()
                }
            }
        }
    }
}