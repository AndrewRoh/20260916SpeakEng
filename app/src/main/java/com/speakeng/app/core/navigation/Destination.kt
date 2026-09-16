package com.speakeng.app.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-level destinations shown in the bottom navigation bar. */
enum class Destination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
) {
    Home("home", com.speakeng.app.R.string.nav_home, Icons.Filled.Home),
    Conversation("conversation", com.speakeng.app.R.string.nav_conversation, Icons.Filled.Chat),
    Pronunciation("pronunciation", com.speakeng.app.R.string.nav_pronunciation, Icons.Filled.GraphicEq),
    Curriculum("curriculum", com.speakeng.app.R.string.nav_curriculum, Icons.Filled.AutoStories),
    Profile("profile", com.speakeng.app.R.string.nav_profile, Icons.Filled.Person),
}
