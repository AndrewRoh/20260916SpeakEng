package com.speakeng.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val RATE_PRESETS = listOf(0.5f, 0.75f, 1.0f)

/**
 * Shared TTS playback controls: play/stop, speed presets, and a repeat toggle. [onPrevious] and
 * [onNext] are optional so screens that play a single item (e.g. one chat message) can omit them,
 * while multi-sentence screens (e.g. reading) pass both.
 */
@Composable
fun PlaybackControlBar(
    isPlaying: Boolean,
    rate: Float,
    isRepeatEnabled: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onRateSelected: (Float) -> Unit,
    onToggleRepeat: () -> Unit,
    modifier: Modifier = Modifier,
    onPrevious: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth().padding(12.dp)) {
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
            if (onPrevious != null) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
                }
            }
            IconButton(onClick = if (isPlaying) onStop else onPlay) {
                Icon(
                    if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Stop" else "Play",
                )
            }
            if (onNext != null) {
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next")
                }
            }
            IconToggleButton(checked = isRepeatEnabled, onCheckedChange = { onToggleRepeat() }) {
                Icon(Icons.Filled.Loop, contentDescription = "Repeat")
            }
        }
    }
}
