package com.speakeng.app.feature.curriculum.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem

@Composable
fun CurriculumScreen(onOpenBooks: () -> Unit, viewModel: CurriculumViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { items ->
        CurriculumContent(items, onOpenBooks, onToggleCompleted = viewModel::toggleCompleted)
    }
}

@Composable
private fun CurriculumContent(
    items: List<CurriculumItem>,
    onOpenBooks: () -> Unit,
    onToggleCompleted: (Long) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable(onClick = onOpenBooks),
            ) {
                Text(text = "영어 책읽기 (TTS)", modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp))
                Text(text = "문장 단위로 읽어주는 책 목록 보기", modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 12.dp))
            }
        }
        items(items, key = { it.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(text = item.title)
                        Text(text = item.level)
                    }
                    Checkbox(checked = item.isCompleted, onCheckedChange = { onToggleCompleted(item.id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurriculumScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                listOf(CurriculumItem(1, "Everyday Greetings", "Beginner", false)),
            ),
        ) { CurriculumContent(it, onOpenBooks = {}, onToggleCompleted = {}) }
    }
}
