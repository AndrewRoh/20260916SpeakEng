package com.speakeng.app.feature.pronunciation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.pronunciation.domain.model.PronunciationResult

@Composable
fun PronunciationScreen(viewModel: PronunciationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { result ->
        PronunciationContent(result = result, onRecordClick = viewModel::onRecordClick)
    }
}

@Composable
private fun PronunciationContent(result: PronunciationResult?, onRecordClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Pronunciation Practice", style = MaterialTheme.typography.titleLarge)
        if (result == null) {
            Text(text = "Tap record and read the sentence aloud.")
        } else {
            Text(text = "Recognized: ${result.recognizedText}")
            Text(text = "Score: ${result.score}")
            Text(text = result.feedback)
        }
        Button(onClick = onRecordClick) { Text("Record") }
    }
}

@Preview(showBackground = true)
@Composable
private fun PronunciationScreenPreview() {
    SpeakEngTheme {
        UiStateContent(uiState = UiState.Success<PronunciationResult?>(null)) {
            PronunciationContent(it, onRecordClick = {})
        }
    }
}
