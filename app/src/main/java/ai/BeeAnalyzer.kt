package com.beevision.app.ai

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.beevision.app.model.FrameCell
import com.beevision.app.model.FrameMap
import com.beevision.app.model.ScanResult
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object BeeAnalyzer {

    fun analyzeFrame(
        context: Context,
        imageUri: Uri?,
        frameType: String = "Дадан 300"
    ): ScanResult {

        if (imageUri == null) {
            return fallbackResult(imageUri, frameType)
        }

        OpenCVLoader.initLocal()

        val inputStream =
            context.contentResolver.openInputStream(imageUri)
                ?: return fallbackResult(imageUri, frameType)

        val bitmap =
            BitmapFactory.decodeStream(inputStream)
                ?: return fallbackResult(imageUri, frameType)

        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGBA2GRAY)

        val meanBrightness = Core.mean(gray).`val`[0]

        val totalCells = estimateCellsFromImage(gray)
        val honeyPercent = estimateHoneyPercent(meanBrightness)
        val broodPercent = estimateBroodPercent(meanBrightness)
        val pollenPercent = 6

        val honeyCells = (totalCells * honeyPercent / 100.0).toInt()
        val broodCells = (totalCells * broodPercent / 100.0).toInt()
        val pollenCells = (totalCells * pollenPercent / 100.0).toInt()

        val emptyCells =
            (totalCells - honeyCells - broodCells - pollenCells)
                .coerceAtLeast(0)

        val colonyScore = calculateColonyScore(
            totalCells = totalCells,
            broodCells = broodCells,
            honeyCells = honeyCells,
            pollenCells = pollenCells,
            emptyCells = emptyCells
        )

        val frameMap = createDemoFrameMap(
            frameType = frameType,
            honeyPercent = honeyPercent,
            broodPercent = broodPercent
        )

        return ScanResult(
            imageUri = imageUri,
            totalCells = totalCells,
            broodCells = broodCells,
            honeyCells = honeyCells,
            pollenCells = pollenCells,
            emptyCells = emptyCells,
            frameType = frameType,
            frameMap = frameMap,
            colonyScore = colonyScore
        )
    }

    private fun estimateCellsFromImage(gray: Mat): Int {
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

        val edges = Mat()
        Imgproc.Canny(blurred, edges, 60.0, 160.0)

        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()

        Imgproc.findContours(
            edges,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        val usefulContours =
            contours.count {
                val area = Imgproc.contourArea(it)
                area in 20.0..900.0
            }

        return (usefulContours * 4)
            .coerceIn(1000, 8500)
    }

    private fun estimateHoneyPercent(brightness: Double): Int =
        when {
            brightness > 165 -> 55
            brightness > 130 -> 42
            else -> 25
        }

    private fun estimateBroodPercent(brightness: Double): Int =
        when {
            brightness > 165 -> 18
            brightness > 130 -> 30
            else -> 45
        }

    private fun calculateColonyScore(
        totalCells: Int,
        broodCells: Int,
        honeyCells: Int,
        pollenCells: Int,
        emptyCells: Int
    ): Int {
        if (totalCells <= 0) return 0

        val broodPercent = broodCells * 100.0 / totalCells
        val honeyPercent = honeyCells * 100.0 / totalCells
        val pollenPercent = pollenCells * 100.0 / totalCells
        val emptyPercent = emptyCells * 100.0 / totalCells

        val broodScore = broodPercent * 1.2
        val honeyScore = honeyPercent * 0.8
        val pollenScore = pollenPercent * 1.5
        val emptyPenalty = emptyPercent * 0.4

        return (broodScore + honeyScore + pollenScore - emptyPenalty)
            .toInt()
            .coerceIn(0, 100)
    }

    private fun createDemoFrameMap(
        frameType: String,
        honeyPercent: Int,
        broodPercent: Int
    ): FrameMap {
        return when (frameType) {

            "Українська" -> FrameMap(
                rows = 3,
                cols = 2,
                cells = listOf(
                    FrameCell(0, 0, "Мед", 85),
                    FrameCell(0, 1, "Мед", 75),

                    FrameCell(1, 0, "Розплід", 90),
                    FrameCell(1, 1, "Розплід", 80),

                    FrameCell(2, 0, "Перга", 65),
                    FrameCell(2, 1, "Порожні", 55)
                )
            )

            "Магазин 145" -> FrameMap(
                rows = 2,
                cols = 3,
                cells = listOf(
                    FrameCell(0, 0, "Мед", 90),
                    FrameCell(0, 1, "Мед", 85),
                    FrameCell(0, 2, "Мед", 80),

                    FrameCell(1, 0, "Порожні", 45),
                    FrameCell(1, 1, "Перга", 60),
                    FrameCell(1, 2, "Порожні", 50)
                )
            )

            else -> {
                if (broodPercent >= 35) {
                    FrameMap(
                        rows = 3,
                        cols = 3,
                        cells = listOf(
                            FrameCell(0, 0, "Мед", 60),
                            FrameCell(0, 1, "Мед", 70),
                            FrameCell(0, 2, "Мед", 55),

                            FrameCell(1, 0, "Розплід", 90),
                            FrameCell(1, 1, "Розплід", 95),
                            FrameCell(1, 2, "Розплід", 80),

                            FrameCell(2, 0, "Перга", 65),
                            FrameCell(2, 1, "Порожні", 40),
                            FrameCell(2, 2, "Порожні", 55)
                        )
                    )
                } else if (honeyPercent >= 45) {
                    FrameMap(
                        rows = 3,
                        cols = 3,
                        cells = listOf(
                            FrameCell(0, 0, "Мед", 90),
                            FrameCell(0, 1, "Мед", 85),
                            FrameCell(0, 2, "Мед", 80),

                            FrameCell(1, 0, "Мед", 70),
                            FrameCell(1, 1, "Розплід", 65),
                            FrameCell(1, 2, "Мед", 60),

                            FrameCell(2, 0, "Перга", 45),
                            FrameCell(2, 1, "Порожні", 50),
                            FrameCell(2, 2, "Порожні", 60)
                        )
                    )
                } else {
                    FrameMap(
                        rows = 3,
                        cols = 3,
                        cells = listOf(
                            FrameCell(0, 0, "Мед", 40),
                            FrameCell(0, 1, "Порожні", 55),
                            FrameCell(0, 2, "Порожні", 60),

                            FrameCell(1, 0, "Розплід", 75),
                            FrameCell(1, 1, "Розплід", 80),
                            FrameCell(1, 2, "Порожні", 45),

                            FrameCell(2, 0, "Перга", 50),
                            FrameCell(2, 1, "Порожні", 70),
                            FrameCell(2, 2, "Порожні", 75)
                        )
                    )
                }
            }
        }
    }

    private fun fallbackResult(
        imageUri: Uri?,
        frameType: String
    ): ScanResult {
        val frameMap = createDemoFrameMap(
            frameType = frameType,
            honeyPercent = 50,
            broodPercent = 30
        )

        return ScanResult(
            imageUri = imageUri,
            totalCells = 5200,
            broodCells = 1800,
            honeyCells = 2600,
            pollenCells = 300,
            emptyCells = 500,
            frameType = frameType,
            frameMap = frameMap,
            colonyScore = 72
        )
    }
}