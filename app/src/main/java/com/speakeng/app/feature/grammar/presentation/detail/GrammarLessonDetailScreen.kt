package com.speakeng.app.feature.grammar.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.speakeng.app.feature.grammar.data.GrammarLessonContent
import com.speakeng.app.feature.grammar.domain.model.GrammarExample

private val RATE_PRESETS = listOf(0.5f, 0.75f, 1.0f)

@Composable
fun GrammarLessonDetailScreen(viewModel: GrammarLessonDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { data ->
        GrammarLessonDetailContent(
            data = data,
            onPlay = viewModel::play,
            onStop = viewModel::stop,
            onToggleCompleted = viewModel::toggleCompleted,
            onPlayAll = { viewModel.playAll(shuffled = false) },
            onPlayRandom = { viewModel.playAll(shuffled = true) },
            onSetRate = viewModel::setRate,
            onToggleListenAndRepeat = viewModel::toggleListenAndRepeat,
            onToggleSingleRepeat = viewModel::toggleSingleRepeat,
        )
    }
}

@Composable
private fun GrammarLessonDetailContent(
    data: GrammarLessonDetailData,
    onPlay: (Int) -> Unit,
    onStop: () -> Unit,
    onToggleCompleted: () -> Unit,
    onPlayAll: () -> Unit,
    onPlayRandom: () -> Unit,
    onSetRate: (Float) -> Unit,
    onToggleListenAndRepeat: () -> Unit,
    onToggleSingleRepeat: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "${data.lesson.id}강 · ${data.lesson.title}", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "완료", style = MaterialTheme.typography.labelLarge)
                Checkbox(checked = data.isCompleted, onCheckedChange = { onToggleCompleted() })
            }
        }
        Text(
            text = data.lesson.explanation,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
        PlaybackControlPanel(
            data = data,
            onPlayAll = onPlayAll,
            onPlayRandom = onPlayRandom,
            onStop = onStop,
            onSetRate = onSetRate,
            onToggleListenAndRepeat = onToggleListenAndRepeat,
            onToggleSingleRepeat = onToggleSingleRepeat,
        )
        val listState = rememberLazyListState()
        LaunchedEffect(data.playingExampleIndex) {
            val index = data.playingExampleIndex ?: return@LaunchedEffect
            listState.animateScrollToItem(index)
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            items(data.lesson.examples, key = { it.english }) { example ->
                val index = data.lesson.examples.indexOf(example)
                ExampleRow(
                    example = example,
                    isPlaying = index == data.playingExampleIndex,
                    isAwaitingRepeat = index == data.playingExampleIndex && data.isAwaitingRepeat,
                    onClick = { if (index == data.playingExampleIndex) onStop() else onPlay(index) },
                )
            }
        }
    }
}

@Composable
private fun PlaybackControlPanel(
    data: GrammarLessonDetailData,
    onPlayAll: () -> Unit,
    onPlayRandom: () -> Unit,
    onStop: () -> Unit,
    onSetRate: (Float) -> Unit,
    onToggleListenAndRepeat: () -> Unit,
    onToggleSingleRepeat: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "재생 속도", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RATE_PRESETS.forEach { rate ->
                    FilterChip(
                        selected = data.rate == rate,
                        onClick = { onSetRate(rate) },
                        label = { Text("${rate}x") },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onPlayAll, enabled = !data.isSequencePlaying) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null)
                    Text(text = " 전체 듣기", modifier = Modifier.padding(start = 4.dp))
                }
                Button(onClick = onPlayRandom, enabled = !data.isSequencePlaying) {
                    Icon(Icons.Filled.Shuffle, contentDescription = null)
                    Text(text = " 랜덤 듣기", modifier = Modifier.padding(start = 4.dp))
                }
                OutlinedButton(onClick = onStop) {
                    Icon(Icons.Filled.Stop, contentDescription = null)
                    Text(text = " 정지", modifier = Modifier.padding(start = 4.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "따라 말하기 모드", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = data.isListenAndRepeatEnabled, onCheckedChange = { onToggleListenAndRepeat() })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "한 문장 반복", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = data.isSingleRepeatEnabled, onCheckedChange = { onToggleSingleRepeat() })
            }
        }
    }
}

@Composable
private fun ExampleRow(example: GrammarExample, isPlaying: Boolean, isAwaitingRepeat: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .let {
                when {
                    isAwaitingRepeat -> it.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    isPlaying -> it.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                    else -> it
                }
            },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp, 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = example.english, style = MaterialTheme.typography.bodyLarge)
                    Text(text = example.korean, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onClick) {
                    Icon(
                        if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                        contentDescription = if (isPlaying) "Stop" else "Listen",
                    )
                }
            }
            if (isAwaitingRepeat) {
                Text(
                    text = "따라 말해보세요",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GrammarLessonDetailScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                GrammarLessonDetailData(
                    lesson = GrammarLessonContent.LESSONS.first(),
                    isCompleted = false,
                    playingExampleIndex = null,
                ),
            ),
        ) {
            GrammarLessonDetailContent(
                data = it,
                onPlay = {},
                onStop = {},
                onToggleCompleted = {},
                onPlayAll = {},
                onPlayRandom = {},
                onSetRate = {},
                onToggleListenAndRepeat = {},
                onToggleSingleRepeat = {},
            )
        }
    }
}
