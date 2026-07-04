package com.beevision.app.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.beevision.app.dataset.DatasetManager
import com.beevision.app.model.ScanResult

@Composable
fun HistoryScreen(
    history: List<ScanResult>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var exportStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "🖼 Історія сканувань",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(modifier = Modifier.weight(1f), onClick = onBack) {
                Text("Назад")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val path = DatasetManager.exportToFile(context, history)
                    exportStatus = "✅ Експортовано:\n$path"

                    Toast.makeText(context, "Файл створено", Toast.LENGTH_SHORT).show()

                    Log.e("BeeVisionDataset", "Saved to: $path")
                }
            ) {
                Text("📤")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    DatasetManager.shareDataset(context, history)
                }
            ) {
                Text("📨")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (exportStatus.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = exportStatus,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (history.isEmpty()) {
            Text("Поки що немає сканувань")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(history) { index, result ->

                    val honeyPercent =
                        if (result.totalCells > 0) result.honeyCells * 100 / result.totalCells else 0
                    val broodPercent =
                        if (result.totalCells > 0) result.broodCells * 100 / result.totalCells else 0
                    val pollenPercent =
                        if (result.totalCells > 0) result.pollenCells * 100 / result.totalCells else 0
                    val emptyPercent =
                        if (result.totalCells > 0) result.emptyCells * 100 / result.totalCells else 0

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Сканування #${index + 1}")

                            Spacer(modifier = Modifier.height(8.dp))

                            if (result.imageUri != null) {
                                AsyncImage(
                                    model = result.imageUri,
                                    contentDescription = "Фото рамки",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text("Комірок: ${result.totalCells}")
                            Text("🍯 Мед: ${result.honeyCells} ($honeyPercent%)")
                            Text("🐝 Розплід: ${result.broodCells} ($broodPercent%)")
                            Text("🌾 Перга: ${result.pollenCells} ($pollenPercent%)")
                            Text("⬜ Порожні: ${result.emptyCells} ($emptyPercent%)")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Тип рамки: ${result.frameType}")
                            Text("Сторона: ${result.frameSide}")
                            Text("Вміст: ${result.mainContent}")
                            Text("Дата: ${result.createdAt}")

                            if (result.comment.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Коментар: ${result.comment}")
                            }
                        }
                    }
                }
            }
        }
    }
}