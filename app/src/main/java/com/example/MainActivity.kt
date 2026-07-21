package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.engine.BackgroundMusicPlayer
import com.example.engine.GameEngine
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitchDarkBg
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var gameEngine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide system status and navigation bars for immersive fullscreen
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        gameEngine = GameEngine(applicationContext)
        
        // Initialize game settings and preferences
        com.example.engine.GameSettings.initialize(applicationContext)
        
        // Initialize background music engine
        BackgroundMusicPlayer.initialize(applicationContext)

        // On startup we do not automatically load, so the user sees the Main Menu first
        // gameEngine.tryLoadGame() is now called on demand from the Main Menu "Continuar partida" button.

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PitchDarkBg)
                ) { innerPadding ->
                    OnboardingScreen(
                        engine = gameEngine,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        BackgroundMusicPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        BackgroundMusicPlayer.resume(applicationContext)
    }
}
