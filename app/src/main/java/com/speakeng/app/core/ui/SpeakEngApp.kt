package com.speakeng.app.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.speakeng.app.core.navigation.SpeakEngNavHost
import com.speakeng.app.core.ui.components.SpeakEngBottomBar
import com.speakeng.app.core.ui.theme.SpeakEngTheme

/** App root composable: theme + Scaffold hosting the bottom nav bar and nav graph. */
@Composable
fun SpeakEngApp() {
    SpeakEngTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { SpeakEngBottomBar(navController) },
        ) { innerPadding ->
            SpeakEngNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
