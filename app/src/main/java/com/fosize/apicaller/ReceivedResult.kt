package com.fosize.apicaller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

/** What was sent — the request text, or null for no request extra at all. */
data class Sent(val request: String?)

/** What Logsizer sent back — or why nothing was sent — for one request. */
data class ReceivedResult(
    val sent: Sent,
    val resultCode: Int,
    /** Every extra of the result, by name, laid out as JSON where it is one. */
    val extras: List<Pair<String, String>>,
    val photoUri: String?,
    val photo: Bitmap?,
    val note: String?
) {
    val resultName: String
        get() = when (resultCode) {
            Activity.RESULT_OK -> "RESULT_OK"
            Activity.RESULT_CANCELED -> "RESULT_CANCELED"
            else -> "code $resultCode"
        }

    companion object {
        fun notStarted(sent: Sent, reason: String) = ReceivedResult(
            sent, resultCode = Int.MIN_VALUE, extras = emptyList(), photoUri = null, photo = null, note = reason
        )
    }
}

/**
 * Reads an activity result the moment it arrives. The photo's uri is readable only
 * while this activity lives, by the grant on the result, so the file is copied into
 * the cache here, as any caller should.
 */
fun readResult(context: Context, sent: Sent, resultCode: Int, data: Intent?): ReceivedResult {
    val extras = data?.extras?.let { bundle ->
        bundle.keySet().sorted().map { key ->
            @Suppress("DEPRECATION")
            key to (bundle.get(key)?.let { value -> if (value is String) prettyJson(value) else value.toString() } ?: "null")
        }
    } ?: emptyList()
    val uri = data?.data
    var photo: Bitmap? = null
    var note: String? = null
    if (uri != null) {
        try {
            val copy = copyPhoto(context, uri)
            photo = decodeForDisplay(copy)
            note = "photo copied to ${copy.path} (${copy.length() / 1024} KB)"
        } catch (e: Exception) {
            note = "photo could not be read: ${e.message}"
        }
    }
    return ReceivedResult(sent, resultCode, extras, uri?.toString(), photo, note)
}

private fun copyPhoto(context: Context, uri: Uri): File {
    val file = File(context.cacheDir, "received/photo.jpg").apply { parentFile!!.mkdirs() }
    context.contentResolver.openInputStream(uri)!!.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file
}

/** The photo scaled down for the screen; a 12-megapixel JPEG decoded whole is not. */
private fun decodeForDisplay(file: File, maxSide: Int = 1600): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.path, bounds)
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
    return BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample })
}
