package com.speakeng.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.speakeng.app.feature.conversation.presentation.ConversationScreen
import com.speakeng.app.feature.curriculum.presentation.CurriculumScreen
import com.speakeng.app.feature.home.presentation.HomeScreen
import com.speakeng.app.feature.profile.presentation.ProfileScreen
import com.speakeng.app.feature.pronunciation.presentation.PronunciationScreen

/** Hosts the 5 top-level screens reachable from the bottom navigation bar. */
@Composable
fun SpeakEngNavHost(
    navController: NavHostController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier,
    ) {
        composable(Destination.Home.route) { HomeScreen() }
        composable(Destination.Conversation.route) { ConversationScreen() }
        composable(Destination.Pronunciation.route) { PronunciationScreen() }
        composable(Destination.Curriculum.route) { CurriculumScreen() }
        composable(Destination.Profile.route) { ProfileScreen() }
    }
}
