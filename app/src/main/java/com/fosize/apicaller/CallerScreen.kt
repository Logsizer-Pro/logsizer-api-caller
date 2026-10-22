package com.fosize.apicaller

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CallerScreen(viewModel: CallerViewModel, installed: Boolean, onSend: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Logsizer API Caller") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (installed) "Logsizer is installed" else "Logsizer is not installed",
                style = MaterialTheme.typography.bodyMedium,
                color = if (installed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            Text(
                text = "$ACTION_MEASURE · extra \"$EXTRA_REQUEST\"",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Presets.all.forEach { preset ->
                    AssistChip(onClick = { viewModel.applyPreset(preset) }, label = { Text(preset.name) })
                }
            }

            OutlinedTextField(
                value = viewModel.request,
                onValueChange = { viewModel.request = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Request") },
                minLines = 4,
                enabled = !viewModel.sendNoExtra,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = viewModel.sendNoExtra, onCheckedChange = { viewModel.sendNoExtra = it })
                Text("Send no request extra at all")
            }

            Button(onClick = onSend, modifier = Modifier.fillMaxWidth()) {
                Text("Send to Logsizer")
            }

            viewModel.result?.let { ResultCard(it) }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultCard(result: ReceivedResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (result.resultCode == Int.MIN_VALUE) "Not started" else result.resultName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "sent: " + (result.sent.request ?: "(no request extra)"),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace
            )
            if (result.extras.isEmpty() && result.resultCode != Int.MIN_VALUE) {
                Text("no extras", style = MaterialTheme.typography.bodySmall)
            }
            result.extras.forEach { (name, value) ->
                Text(name, style = MaterialTheme.typography.labelLarge)
                SelectionContainer {
                    Text(value, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                }
            }
            result.photoUri?.let { Text("data: $it", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace) }
            result.photo?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "the photo Logsizer returned",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
            result.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
