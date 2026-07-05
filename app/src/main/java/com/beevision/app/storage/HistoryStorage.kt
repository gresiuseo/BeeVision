package com.beevision.app.storage

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.beevision.app.model.ScanResult
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("beevision")

object HistoryStorage {

    private val HISTORY_KEY = stringPreferencesKey("history")

    suspend fun saveHistory(
        context: Context,
        history: List<ScanResult>
    ) {
        val array = JSONArray()

        history.forEach { item ->
            val obj = JSONObject()

            obj.put("imageUri", item.imageUri?.toString())
            obj.put("totalCells", item.totalCells)
            obj.put("broodCells", item.broodCells)
            obj.put("honeyCells", item.honeyCells)
            obj.put("pollenCells", item.pollenCells)
            obj.put("emptyCells", item.emptyCells)
            obj.put("createdAt", item.createdAt)
            obj.put("frameType", item.frameType)
            obj.put("frameSide", item.frameSide)
            obj.put("mainContent", item.mainContent)
            obj.put("comment", item.comment)
            obj.put("colonyScore", item.colonyScore)

            array.put(obj)
        }

        context.dataStore.edit { prefs ->
            prefs[HISTORY_KEY] = array.toString()
        }
    }

    suspend fun loadHistory(
        context: Context
    ): MutableList<ScanResult> {
        val prefs = context.dataStore.data.first()
        val json = prefs[HISTORY_KEY] ?: return mutableListOf()

        val array = try {
            JSONArray(json)
        } catch (e: Exception) {
            context.dataStore.edit { prefs ->
                prefs.remove(HISTORY_KEY)
            }
            return mutableListOf()
        }

        val list = mutableListOf<ScanResult>()

        for (i in 0 until array.length()) {
            val item = array.opt(i)

            if (item is JSONObject) {
                list.add(
                    ScanResult(
                        imageUri = item.optString("imageUri")
                            .takeIf { it.isNotBlank() && it != "null" }
                            ?.let { Uri.parse(it) },
                        totalCells = item.optInt("totalCells"),
                        broodCells = item.optInt("broodCells"),
                        honeyCells = item.optInt("honeyCells"),
                        pollenCells = item.optInt("pollenCells"),
                        emptyCells = item.optInt("emptyCells"),
                        createdAt = item.optString("createdAt"),
                        frameType = item.optString("frameType", "Невідомо"),
                        frameSide = item.optString("frameSide", "Ліва"),
                        mainContent = item.optString("mainContent", "Змішана"),
                        comment = item.optString("comment", ""),
                        colonyScore = item.optInt("colonyScore", 0)
                    )
                )
            }
        }

        return list
    }
}