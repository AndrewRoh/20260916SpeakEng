package com.speakeng.app.feature.reading.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.reading.domain.model.ReadingSentence

private val RATE_PRESETS = listOf(0.5f, 0.75f, 1.0f)

@Composable
fun ReadingScreen(viewModel: ReadingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { data ->
        ReadingContent(
            data = data,
            onPlay = viewModel::play,
            onStop = viewModel::stop,
            onNext = viewModel::next,
            onPrevious = viewModel::previous,
            onRateSelected = viewModel::setRate,
            onToggleRepeat = viewModel::toggleRepeat,
        )
    }
}

@Composable
private fun ReadingContent(
    data: ReadingData,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRateSelected: (Float) -> Unit,
    onToggleRepeat: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            items(data.sentences, key = { it.index }) { sentence ->
                SentenceRow(sentence = sentence, isCurrent = sentence.index == data.currentIndex)
            }
        }
        ReadingControlBar(
            isPlaying = data.isPlaying,
            rate = data.rate,
            isRepeatEnabled = data.isRepeatEnabled,
            onPlay = onPlay,
            onStop = onStop,
            onNext = onNext,
            onPrevious = onPrevious,
            onRateSelected = onRateSelected,
            onToggleRepeat = onToggleRepeat,
        )
    }
}

@Composable
private fun SentenceRow(sentence: ReadingSentence, isCurrent: Boolean) {
    val background = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Text(
        text = sentence.text,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(12.dp),
    )
}

@Composable
private fun ReadingControlBar(
    isPlaying: Boolean,
    rate: Float,
    isRepeatEnabled: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRateSelected: (Float) -> Unit,
    onToggleRepeat: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            RATE_PRESETS.forEach { preset ->
                FilterChip(
                    selected = rate == preset,
                    onClick = { onRateSelected(preset) },
                    label = { Text("${preset}x") },
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous sentence")
            }
            IconButton(onClick = if (isPlaying) onStop else onPlay) {
                Icon(
                    if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Stop" else "Play",
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next sentence")
            }
            IconToggleButton(checked = isRepeatEnabled, onCheckedChange = { onToggleRepeat() }) {
                Icon(Icons.Filled.Loop, contentDescription = "Repeat current sentence")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                ReadingData(
                    sentences = listOf(
                        ReadingSentence(0, "Today was a beautiful day."),
                        ReadingSentence(1, "I went for a walk in the park."),
                    ),
                    currentIndex = 0,
                    isPlaying = false,
                    rate = 1f,
                    isRepeatEnabled = false,
                ),
            ),
        ) { data ->
            ReadingContent(data, onPlay = {}, onStop = {}, onNext = {}, onPrevious = {}, onRateSelected = {}, onToggleRepeat = {})
        }
    }
}
