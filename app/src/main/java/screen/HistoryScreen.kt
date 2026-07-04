package com.beevision.app.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.beevision.app.dataset.DatasetManager
import com.beevision.app.model.ScanResult

@Composable
fun HistoryScreen(
    history: List<ScanResult>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                top = 42.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Text("🖼 Історія сканувань")

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onBack
            ) {
                Text("Назад")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val json = DatasetManager.buildExportJson(history)
                    android.util.Log.d("BeeVisionDataset", json)
                }
            ) {
                Text("📤 Експорт")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (history.isEmpty()) {
            Text("Поки що немає сканувань")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(history) { index, result ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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
                            Text("Мед: ${result.honeyCells}")
                            Text("Розплід: ${result.broodCells}")
                            Text("Перга: ${result.pollenCells}")
                            Text("Порожні: ${result.emptyCells}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Тип рамки: ${result.frameType}")
                            Text("Сторона: ${result.frameSide}")
                            Text("Вміст: ${result.mainContent}")
                            Text("Дата: ${result.createdAt}")

                            if (result.comment.isNotBlank()) {
                                Text("Коментар: ${result.comment}")
                            }
                        }
                    }
                }
            }
        }
    }
}