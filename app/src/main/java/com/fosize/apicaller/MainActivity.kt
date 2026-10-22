package com.fosize.apicaller

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme

/**
 * The one screen: it starts Logsizer for a result — `registerForActivityResult`, so
 * Logsizer learns who asks and names it on its first screen — and shows what comes
 * back.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: CallerViewModel by viewModels()

    private val measure = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val sent = viewModel.sent ?: return@registerForActivityResult
        viewModel.result = readResult(this, sent, result.resultCode, result.data)
        viewModel.sent = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CallerScreen(
                    viewModel = viewModel,
                    installed = isLogsizerInstalled(),
                    onSend = { send() }
                )
            }
        }
    }

    private fun send() {
        val sent = Sent(viewModel.requestToSend())
        viewModel.sent = sent
        try {
            measure.launch(measureIntent(sent.request))
        } catch (e: ActivityNotFoundException) {
            viewModel.sent = null
            viewModel.result = ReceivedResult.notStarted(sent, "Logsizer is not installed, or does not answer $ACTION_MEASURE")
        }
    }

    private fun isLogsizerInstalled(): Boolean =
        packageManager.resolveActivity(Intent(ACTION_MEASURE).setPackage(LOGSIZER_PACKAGE), 0) != null
}
