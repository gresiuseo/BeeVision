package com.beevision.app.dataset

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.beevision.app.model.ScanResult
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object DatasetManager {

    fun buildExportJson(
        history: List<ScanResult>
    ): String {

        val array = JSONArray()

        history.forEach { item ->

            val obj = JSONObject()

            obj.put("imageUri", item.imageUri?.toString())
            obj.put("createdAt", item.createdAt)
            obj.put("frameType", item.frameType)
            obj.put("frameSide", item.frameSide)
            obj.put("mainContent", item.mainContent)
            obj.put("comment", item.comment)

            obj.put("totalCells", item.totalCells)
            obj.put("broodCells", item.broodCells)
            obj.put("honeyCells", item.honeyCells)
            obj.put("pollenCells", item.pollenCells)
            obj.put("emptyCells", item.emptyCells)

            array.put(obj)
        }

        return array.toString(2)
    }

    fun exportToFile(
        context: Context,
        history: List<ScanResult>
    ): String {

        val json = buildExportJson(history)

        val file = File(
            context.filesDir,
            "beevision_dataset.json"
        )

        file.writeText(json)

        return file.absolutePath
    }

    fun shareDataset(
        context: Context,
        history: List<ScanResult>
    ) {

        val path = exportToFile(
            context,
            history
        )

        val file = File(path)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(
            Intent.ACTION_SEND
        ).apply {

            type = "application/json"

            putExtra(
                Intent.EXTRA_STREAM,
                uri
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        context.startActivity(
            Intent.createChooser(
                intent,
                "Поділитися датасетом BeeVision"
            )
        )
    }
}