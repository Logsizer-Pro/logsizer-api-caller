package com.fosize.apicaller

import android.content.Intent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/** Logsizer's package: the intent is addressed to it, so no other app can answer the action. */
const val LOGSIZER_PACKAGE = "com.fosize.android"

/** The action of Logsizer's public API, and the extra the request travels in. */
const val ACTION_MEASURE = "com.fosize.android.action.MEASURE"
const val EXTRA_REQUEST = "request"

/** A request worth sending with one tap; the text can be edited before it goes. */
class Preset(val name: String, val request: String)

object Presets {
    val all = listOf(
        Preset("Empty", "{}"),
        Preset("By the piece", """{"method": "by_the_piece", "length_cm": 300, "title": "Lot 142"}"""),
        Preset(
            "Pile",
            """{"method": "pile", "length_cm": 300, "title": "Lot 142", "metadata": {"lot": 142, "yard": "north"}}"""
        ),
        Preset(
            "Manual pile",
            """{"method": "manual_pile", "length_cm": 250, "title": "Lot 142", "metadata": {"lot": 142, "yard": "north"}}"""
        ),
        Preset("Raw", """{"result": "raw"}"""),
        Preset("Malformed", """{"method": "unknown"}""")
    )
}

/** The intent that starts Logsizer; a null [request] sends no request extra at all. */
fun measureIntent(request: String?): Intent =
    Intent(ACTION_MEASURE).setPackage(LOGSIZER_PACKAGE).apply {
        if (request != null) putExtra(EXTRA_REQUEST, request)
    }

private val pretty = Json { prettyPrint = true }

/** [text] laid out as JSON when it is one, as it came otherwise. */
fun prettyJson(text: String): String = try {
    pretty.encodeToString(JsonElement.serializer(), Json.parseToJsonElement(text))
} catch (e: Exception) {
    text
}
