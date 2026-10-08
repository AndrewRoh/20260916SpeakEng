package com.speakeng.app.feature.grammar.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.speakeng.app.feature.grammar.data.GrammarLessonContent
import com.speakeng.app.feature.grammar.domain.model.GrammarExample

@Composable
fun GrammarLessonDetailScreen(viewModel: GrammarLessonDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { data ->
        GrammarLessonDetailContent(
            data = data,
            onPlay = viewModel::play,
            onStop = viewModel::stop,
            onToggleCompleted = viewModel::toggleCompleted,
        )
    }
}

@Composable
private fun GrammarLessonDetailContent(
    data: GrammarLessonDetailData,
    onPlay: (Int) -> Unit,
    onStop: () -> Unit,
    onToggleCompleted: () -> Unit,
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(data.lesson.examples, key = { it.english }) { example ->
                val index = data.lesson.examples.indexOf(example)
                ExampleRow(
                    example = example,
                    isPlaying = index == data.playingExampleIndex,
                    onClick = { if (index == data.playingExampleIndex) onStop() else onPlay(index) },
                )
            }
        }
    }
}

@Composable
private fun ExampleRow(example: GrammarExample, isPlaying: Boolean, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp, 12.dp),
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
        ) { GrammarLessonDetailContent(it, onPlay = {}, onStop = {}, onToggleCompleted = {}) }
    }
}
