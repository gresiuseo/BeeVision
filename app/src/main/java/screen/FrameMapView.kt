package com.beevision.app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beevision.app.model.FrameMap

@Composable
fun FrameMapView(
    frameMap: FrameMap
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("🧠 AI-карта рамки")

        Spacer(modifier = Modifier.height(8.dp))

        for (row in 0 until frameMap.rows) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in 0 until frameMap.cols) {
                    val cell = frameMap.cells.firstOrNull {
                        it.row == row && it.col == col
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp)
                            .padding(3.dp)
                            .background(
                                color = when (cell?.type) {
                                    "Мед" -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                                    "Розплід" -> androidx.compose.ui.graphics.Color(0xFF8D6E63)
                                    "Перга" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                    "Порожні" -> androidx.compose.ui.graphics.Color(0xFFE0E0E0)
                                    else -> androidx.compose.ui.graphics.Color(0xFFBDBDBD)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (cell?.type) {
                                "Мед" -> "🍯\n${cell.percent}%"
                                "Розплід" -> "🐝\n${cell.percent}%"
                                "Перга" -> "🌾\n${cell.percent}%"
                                "Порожні" -> "⬜\n${cell.percent}%"
                                else -> "?"
                            }
                        )
                    }
                }
            }
        }
    }
}