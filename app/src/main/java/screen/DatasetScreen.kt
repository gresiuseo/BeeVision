package com.beevision.app.screen

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
    var frameSide by remember { mutableStateOf("A") }
    var mainContent by remember { mutableStateOf("Змішана") }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("🧬 Дані для навчання AI")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Тип рамки")
        Row {
            listOf("Дадан 300", "Магазин 145", "Рута").forEach { item ->
                Button(
                    onClick = { frameType = item },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Сторона")
        Row {
            listOf("A", "B").forEach { item ->
                Button(
                    onClick = { frameSide = item },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Основний вміст")
        Column {
            listOf("Мед", "Розплід", "Перга", "Змішана", "Суш").forEach { item ->
                Button(
                    onClick = { mainContent = item },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(item)
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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onContinue(frameType, frameSide, mainContent, comment)
            }
        ) {
            Text("Продовжити до камери")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Назад")
        }
    }
}