package com.speakeng.app.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.home.domain.model.HomeSummary

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { summary ->
        HomeContent(summary)
    }
}

@Composable
private fun HomeContent(summary: HomeSummary) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Welcome back, ${summary.userName}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Streak: ${summary.streakDays} days")
        Text(text = "Completed lessons: ${summary.completedLessons}")
        Text(text = "Today's goal")
        LinearProgressIndicator(progress = { summary.todayGoalProgress })
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                HomeSummary(userName = "Alex", streakDays = 5, todayGoalProgress = 0.6f, completedLessons = 12),
            ),
        ) { HomeContent(it) }
    }
}
