package com.sumeyyaterzi.babymoments.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun copyUriToInternalStorage(context: Context, uri: Uri?): String? {
    if (uri == null) return null
    return withContext(Dispatchers.IO) {
        val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
        val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val file = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
        input.use { inputStream ->
            FileOutputStream(file).use { output -> inputStream.copyTo(output) }
        }
        file.absolutePath
    }
}
