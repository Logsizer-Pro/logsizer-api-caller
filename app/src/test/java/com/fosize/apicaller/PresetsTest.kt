package com.fosize.apicaller

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

/** The presets are what gets sent most; they have to be what the contract expects. */
class PresetsTest {

    private fun parsed(text: String): JsonObject = Json.parseToJsonElement(text).jsonObject

    private fun preset(name: String) = Presets.all.first { it.name == name }.request

    @Test
    fun `the presets name the contract's values`() {
        assertEquals("{}", preset("Empty"))
        assertEquals("by_the_piece", parsed(preset("By the piece"))["method"]!!.jsonPrimitive.content)
        assertEquals("pile", parsed(preset("Pile"))["method"]!!.jsonPrimitive.content)
        assertEquals("manual_pile", parsed(preset("Manual pile"))["method"]!!.jsonPrimitive.content)
        assertEquals("raw", parsed(preset("Raw"))["result"]!!.jsonPrimitive.content)
        assertEquals("300", parsed(preset("By the piece"))["length_cm"]!!.jsonPrimitive.content)
    }

    @Test
    fun `the pile presets carry a metadata tag to be echoed`() {
        for (name in listOf("Pile", "Manual pile")) {
            val metadata = parsed(preset(name))["metadata"]!!.jsonObject
            assertEquals("142", metadata["lot"]!!.jsonPrimitive.content)
            assertEquals("north", metadata["yard"]!!.jsonPrimitive.content)
        }
    }

    @Test
    fun `the malformed preset is a value the contract refuses`() {
        assertEquals("unknown", parsed(preset("Malformed"))["method"]!!.jsonPrimitive.content)
    }

    @Test
    fun `pretty printing lays JSON out and leaves the rest alone`() {
        assertEquals("{\n    \"a\": 1\n}", prettyJson("""{"a":1}"""))
        assertEquals("not json", prettyJson("not json"))
    }
}
