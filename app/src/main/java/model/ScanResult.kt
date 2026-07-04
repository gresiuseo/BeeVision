package com.beevision.app.model

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScanResult(
    val imageUri: Uri?,
    val totalCells: Int,
    val broodCells: Int,
    val honeyCells: Int,
    val pollenCells: Int,
    val emptyCells: Int,
    val frameType: String = "Невідомо",
    val frameSide: String = "A",
    val mainContent: String = "Змішана",
    val comment: String = "",
    val createdAt: String = currentDateTime()
)

fun currentDateTime(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date())
}