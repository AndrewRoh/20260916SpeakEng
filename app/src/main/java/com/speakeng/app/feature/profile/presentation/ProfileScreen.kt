package com.speakeng.app.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.speakeng.app.feature.profile.domain.model.UserProfile

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { profile -> ProfileContent(profile) }
}

@Composable
private fun ProfileContent(profile: UserProfile) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = profile.userName, style = MaterialTheme.typography.titleLarge)
        Text(text = "Conversations: ${profile.totalConversations}")
        Text(text = "Minutes practiced: ${profile.totalMinutesPracticed}")
        Text(text = "Average pronunciation score: ${profile.averagePronunciationScore}")
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                UserProfile(userName = "Alex", totalConversations = 8, totalMinutesPracticed = 42, averagePronunciationScore = 76),
            ),
        ) { ProfileContent(it) }
    }
}
