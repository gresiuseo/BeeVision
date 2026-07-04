package com.beevision.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.beevision.app.screen.HomeScreen
import com.beevision.app.ui.theme.BeeVisionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BeeVisionTheme {
                Scaffold { _ ->
                    HomeScreen()
                }
            }
        }
    }
}