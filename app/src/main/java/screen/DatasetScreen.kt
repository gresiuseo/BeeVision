package com.beevision.app.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DatasetScreen(
    onBack: () -> Unit,
    onContinue: (
        frameType: String,
        frameSide: String,
        mainContent: String,
        comment: String
    ) -> Unit
) {
    var frameType by remember { mutableStateOf("Дадан 300") }
    var frameSide by remember { mutableStateOf("Ліва") }
    var mainContent by remember { mutableStateOf("Змішана") }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 20.dp,
                top = 42.dp,
                end = 20.dp,
                bottom = 24.dp
            )
    ) {
        Text(
            text = "🧬 Дані для навчання AI",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Тип рамки")

        Column {
            listOf(
                "Дадан 300",
                "Магазин 145",
                "Рута",
                "Українська"
            ).forEach { item ->
                Button(
                    onClick = { frameType = item },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        if (item == frameType) "✅ $item" else item
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Сторона рамки")

        Row {
            listOf("Ліва", "Права").forEach { item ->
                Button(
                    onClick = { frameSide = item },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        if (item == frameSide) "✅ $item" else item
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Основний вміст")

        Column {
            listOf(
                "Мед",
                "Розплід",
                "Перга",
                "Змішана",
                "Суш"
            ).forEach { item ->
                Button(
                    onClick = { mainContent = item },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        if (item == mainContent) "✅ $item" else item
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Коментар") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (frameType == "Українська")
                    "📱 Українська рамка: на екрані камери тримай телефон вертикально"
                else
                    "📱 Цей тип рамки: на екрані камери тримай телефон горизонтально",
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onContinue(
                    frameType,
                    frameSide,
                    mainContent,
                    comment
                )
            }
        ) {
            Text("📷 Продовжити до камери")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("⬅ Назад")
        }
    }
}