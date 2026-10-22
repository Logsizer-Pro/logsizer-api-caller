package com.fosize.apicaller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** The screen's state, kept through a rotation: what is about to be sent, and what came back last. */
class CallerViewModel : ViewModel() {

    var request by mutableStateOf(Presets.all.first().request)

    /** Start Logsizer with no request extra at all, which the contract allows. */
    var sendNoExtra by mutableStateOf(false)

    /** The request on its way, until the result lands. */
    var sent: Sent? = null

    var result by mutableStateOf<ReceivedResult?>(null)

    fun applyPreset(preset: Preset) {
        request = preset.request
    }

    /** What the next tap on Send starts, or null for no request extra. */
    fun requestToSend(): String? = if (sendNoExtra) null else request
}
