package com.speakeng.app.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.speakeng.app.core.common.UiState

/** Renders the common Loading/Error frames and delegates the Success case to [onSuccess]. */
@Composable
fun <T> UiStateContent(
    uiState: UiState<T>,
    modifier: Modifier = Modifier,
    onSuccess: @Composable (T) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp),
            )
            is UiState.Success -> onSuccess(uiState.data)
        }
    }
}
