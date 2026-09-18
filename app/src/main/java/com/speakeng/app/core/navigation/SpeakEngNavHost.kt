package com.speakeng.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.speakeng.app.feature.conversation.presentation.ConversationScreen
import com.speakeng.app.feature.curriculum.presentation.CurriculumScreen
import com.speakeng.app.feature.home.presentation.HomeScreen
import com.speakeng.app.feature.profile.presentation.ProfileScreen
import com.speakeng.app.feature.pronunciation.presentation.PronunciationScreen
import com.speakeng.app.feature.reading.presentation.BookListScreen
import com.speakeng.app.feature.reading.presentation.ReadingScreen
import java.net.URLEncoder

private const val BOOK_LIST_ROUTE = "bookList"
private const val READING_ROUTE = "reading/{bookId}"

/** Hosts the 5 top-level screens reachable from the bottom navigation bar, plus the reading flow pushed from Curriculum. */
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
        composable(Destination.Curriculum.route) {
            CurriculumScreen(onOpenBooks = { navController.navigate(BOOK_LIST_ROUTE) })
        }
        composable(Destination.Profile.route) { ProfileScreen() }

        composable(BOOK_LIST_ROUTE) {
            BookListScreen(onBookClick = { bookId ->
                navController.navigate("reading/${URLEncoder.encode(bookId, Charsets.UTF_8.name())}")
            })
        }
        composable(
            route = READING_ROUTE,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
        ) {
            ReadingScreen()
        }
    }
}
