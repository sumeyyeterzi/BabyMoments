package com.sumeyyaterzi.babymoments.utils

import android.content.Context
import android.util.Log
import com.sumeyyaterzi.babymoments.data.WHOData
import kotlinx.serialization.json.Json
import java.io.IOException

object WHODataParser {

    private var cachedData: WHOData? = null
    private const val TAG = "WHODataParser"

    fun loadWHOData(context: Context): WHOData {
        if (cachedData != null) {
            Log.d(TAG, "WHO data loaded from cache")
            return cachedData!!
        }

        return try {
            Log.d(TAG, "Loading WHO data from assets...")
            val jsonString = context.assets.open("who_growth_standards.json")
                .bufferedReader()
                .use { it.readText() }

            Log.d(TAG, "JSON loaded, parsing...")
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            cachedData = json.decodeFromString<WHOData>(jsonString)
            Log.d(TAG, "WHO data parsed successfully! Male: ${cachedData?.male?.size}, Female: ${cachedData?.female?.size}")
            cachedData!!
        } catch (e: IOException) {
            Log.e(TAG, "IOException loading WHO data", e)
            WHOData(male = emptyList(), female = emptyList())
        } catch (e: Exception) {
            Log.e(TAG, "Exception parsing WHO data", e)
            WHOData(male = emptyList(), female = emptyList())
        }
    }
}