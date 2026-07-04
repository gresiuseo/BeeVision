package com.beevision.app.screen

import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beevision.app.model.ScanResult

@Composable
fun ResultScreen(
    imageUri: Uri?,
    result: ScanResult,
    onBackHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("📊 Результат аналізу")

        Spacer(modifier = Modifier.height(16.dp))

        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Фото рамки",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Усього комірок: ${result.totalCells}")
                Text("Розплід: ${result.broodCells}")
                Text("Мед: ${result.honeyCells}")
                Text("Перга: ${result.pollenCells}")
                Text("Порожні: ${result.emptyCells}")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Тип рамки: ${result.frameType}")
                Text("Сторона: ${result.frameSide}")
                Text("Вміст: ${result.mainContent}")
                Text("Дата: ${result.createdAt}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBackHome
        ) {
            Text("На головну")
        }
    }
}