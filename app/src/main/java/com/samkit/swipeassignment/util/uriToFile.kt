package com.samkit.swipeassignment.util

import android.content.Context
import android.net.Uri
import java.io.File

fun uriToFile(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "picked_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file
}