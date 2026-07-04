package com.beevision.app

import com.beevision.app.screen.DatasetScreen
import com.beevision.app.ai.BeeAnalyzer
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.beevision.app.storage.HistoryStorage
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.beevision.app.model.ScanResult
import com.beevision.app.screen.CameraScreen
import com.beevision.app.screen.HistoryScreen
import com.beevision.app.screen.HomeScreen
import com.beevision.app.screen.ResultScreen
import com.beevision.app.ui.theme.BeeVisionTheme

class MainActivity : ComponentActivity() {

    sealed class Screen {
        data object Home : Screen()
        data object Camera : Screen()
        data object History : Screen()
        data object Dataset : Screen()
        data class Result(
            val imageUri: Uri?,
            val result: ScanResult
        ) : Screen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BeeVisionTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                val history = remember { mutableStateListOf<ScanResult>() }
                var selectedFrameType by remember { mutableStateOf("Дадан 300") }
                var selectedFrameSide by remember { mutableStateOf("A") }
                var selectedMainContent by remember { mutableStateOf("Змішана") }
                var selectedComment by remember { mutableStateOf("") }
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    val saved = HistoryStorage.loadHistory(context)

                    history.clear()
                    history.addAll(saved)
                }

                when (val screen = currentScreen) {
                    Screen.Home -> HomeScreen(
                        datasetCount = history.size,
                        onScanClick = { currentScreen = Screen.Dataset },
                        onHistoryClick = { currentScreen = Screen.History }
                    )
                    Screen.Dataset -> DatasetScreen(
                        onBack = { currentScreen = Screen.Home },
                        onContinue = { frameType, frameSide, mainContent, comment ->
                            selectedFrameType = frameType
                            selectedFrameSide = frameSide
                            selectedMainContent = mainContent
                            selectedComment = comment
                            currentScreen = Screen.Camera
                        }
                    )
                    Screen.Camera -> CameraScreen(
                        onBack = { currentScreen = Screen.Home },
                        onPhotoCaptured = { imageUri ->
                            val aiResult = BeeAnalyzer.analyzeFrame(
                                context = this@MainActivity,
                                imageUri = imageUri
                            )

                            val demoResult = aiResult.copy(
                                frameType = selectedFrameType,
                                frameSide = selectedFrameSide,
                                mainContent = selectedMainContent,
                                comment = selectedComment
                            )

                            history.add(0, demoResult)
                            scope.launch {
                                HistoryStorage.saveHistory(
                                    context,
                                    history
                                )
                            }
                            currentScreen = Screen.Result(
                                imageUri = imageUri,
                                result = demoResult
                            )
                        }
                    )

                    Screen.History -> HistoryScreen(
                        history = history,
                        onBack = { currentScreen = Screen.Home }
                    )

                    is Screen.Result -> ResultScreen(
                        imageUri = screen.imageUri,
                        result = screen.result,
                        onBackHome = { currentScreen = Screen.Home }
                    )
                }
            }
        }
    }
}