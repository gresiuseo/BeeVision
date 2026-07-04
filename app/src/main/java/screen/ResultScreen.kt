package com.beevision.app.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.beevision.app.model.ScanResult

@Composable
fun ResultScreen(
    imageUri: Uri?,
    result: ScanResult,
    onBackHome: () -> Unit
) {

    val honeyPercent =
        if (result.totalCells > 0)
            result.honeyCells * 100 / result.totalCells
        else 0

    val broodPercent =
        if (result.totalCells > 0)
            result.broodCells * 100 / result.totalCells
        else 0

    val pollenPercent =
        if (result.totalCells > 0)
            result.pollenCells * 100 / result.totalCells
        else 0

    val emptyPercent =
        if (result.totalCells > 0)
            result.emptyCells * 100 / result.totalCells
        else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "📊 Результат аналізу",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (imageUri != null) {

            AsyncImage(
                model = imageUri,
                contentDescription = "Фото рамки",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Усього комірок: ${result.totalCells}"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    "🐝 Розплід: ${result.broodCells} ($broodPercent%)"
                )

                Text(
                    "🍯 Мед: ${result.honeyCells} ($honeyPercent%)"
                )

                Text(
                    "🌾 Перга: ${result.pollenCells} ($pollenPercent%)"
                )

                Text(
                    "⬜ Порожні: ${result.emptyCells} ($emptyPercent%)"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text("Тип рамки: ${result.frameType}")
                Text("Сторона: ${result.frameSide}")
                Text("Вміст: ${result.mainContent}")
                Text("Дата: ${result.createdAt}")

                if (result.comment.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        "Коментар: ${result.comment}"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBackHome
        ) {
            Text("На головну")
        }
    }
}