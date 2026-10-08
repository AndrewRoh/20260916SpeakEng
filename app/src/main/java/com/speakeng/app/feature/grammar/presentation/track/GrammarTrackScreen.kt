package com.speakeng.app.feature.grammar.presentation.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import com.speakeng.app.feature.grammar.domain.model.GrammarLessonState

@Composable
fun GrammarTrackScreen(
    onLessonClick: (Int) -> Unit,
    viewModel: GrammarTrackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { lessons ->
        GrammarTrackContent(
            lessons = lessons,
            onLessonClick = onLessonClick,
            onToggleCompleted = viewModel::toggleCompleted,
        )
    }
}

@Composable
private fun GrammarTrackContent(
    lessons: List<GrammarLessonState>,
    onLessonClick: (Int) -> Unit,
    onToggleCompleted: (Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(lessons, key = { it.lesson.id }) { state ->
            GrammarLessonRow(
                state = state,
                onClick = { onLessonClick(state.lesson.id) },
                onToggleCompleted = { onToggleCompleted(state.lesson.id) },
            )
        }
    }
}

@Composable
private fun GrammarLessonRow(
    state: GrammarLessonState,
    onClick: () -> Unit,
    onToggleCompleted: () -> Unit,
) {
    val lesson = state.lesson
    Card(
        onClick = onClick,
        enabled = state.isUnlocked,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Day ${lesson.dayNumber} · ${lesson.id}강", style = MaterialTheme.typography.labelMedium)
                Text(text = lesson.title, style = MaterialTheme.typography.bodyLarge)
            }
            if (state.isUnlocked) {
                Checkbox(checked = state.isCompleted, onCheckedChange = { onToggleCompleted() })
            } else {
                Icon(Icons.Filled.Lock, contentDescription = "Locked until day ${lesson.dayNumber}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GrammarTrackScreenPreview() {
    SpeakEngTheme {
        val lessons = GrammarLessonContent.LESSONS.take(4).mapIndexed { index, lesson ->
            GrammarLessonState(lesson = lesson, isUnlocked = index < 2, isCompleted = index == 0)
        }
        UiStateContent(uiState = UiState.Success(lessons)) {
            GrammarTrackContent(lessons = it, onLessonClick = {}, onToggleCompleted = {})
        }
    }
}
