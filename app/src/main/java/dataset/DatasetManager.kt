package com.beevision.app.dataset

import android.content.Context
import com.beevision.app.model.ScanResult
import org.json.JSONArray
import org.json.JSONObject

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
}