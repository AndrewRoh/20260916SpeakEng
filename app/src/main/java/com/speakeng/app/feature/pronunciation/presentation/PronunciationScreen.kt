@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.speakeng.app.feature.pronunciation.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.ui.components.PlaybackControlBar
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.pronunciation.domain.model.PronunciationResult
import com.speakeng.app.feature.pronunciation.domain.model.WordMatchResult
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.model.BookSource
import com.speakeng.app.feature.reading.domain.model.ReadingSentence
import kotlin.math.roundToInt

@Composable
fun PronunciationScreen(viewModel: PronunciationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.onMicPermissionResult(granted)
        if (granted) viewModel.startListening()
    }

    LaunchedEffect(Unit) {
        viewModel.onMicPermissionResult(hasRecordAudioPermission(context))
    }

    UiStateContent(uiState = uiState) { data ->
        PronunciationContent(
            data = data,
            onSelectBook = viewModel::selectBook,
            onSelectSentence = viewModel::selectSentence,
            onListenToSentence = viewModel::listenToSentence,
            onStopTts = viewModel::stopTts,
            onTtsRateSelected = viewModel::setTtsRate,
            onToggleTtsRepeat = viewModel::toggleTtsRepeat,
            onMicClick = {
                if (data.hasRecordPermission) {
                    viewModel.startListening()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            onStopListening = viewModel::stopListening,
            onOpenSettings = { openAppSettings(context) },
        )
    }
}

private fun hasRecordAudioPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
    context.startActivity(intent)
}

@Composable
private fun PronunciationContent(
    data: PronunciationData,
    onSelectBook: (String) -> Unit,
    onSelectSentence: (Int) -> Unit,
    onListenToSentence: () -> Unit,
    onStopTts: () -> Unit,
    onTtsRateSelected: (Float) -> Unit,
    onToggleTtsRepeat: () -> Unit,
    onMicClick: () -> Unit,
    onStopListening: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Pronunciation Practice", style = MaterialTheme.typography.titleLarge)

        BookPicker(books = data.books, selectedBookId = data.selectedBook?.id, onSelectBook = onSelectBook)

        if (data.sentences.isEmpty()) {
            Text(text = "Select a book above to start practicing.")
            return@Column
        }

        SentenceNavigator(
            sentences = data.sentences,
            selectedIndex = data.selectedSentenceIndex,
            onSelectSentence = onSelectSentence,
        )

        Text(text = "들어보기 (Listen)", style = MaterialTheme.typography.titleMedium)
        PlaybackControlBar(
            isPlaying = data.isPlayingTts,
            rate = data.ttsRate,
            isRepeatEnabled = data.isTtsRepeatEnabled,
            onPlay = onListenToSentence,
            onStop = onStopTts,
            onRateSelected = onTtsRateSelected,
            onToggleRepeat = onToggleTtsRepeat,
        )

        Text(text = "따라 말하기 (Speak)", style = MaterialTheme.typography.titleMedium)
        if (!data.hasRecordPermission) {
            PermissionNotice(onRequestPermission = onMicClick, onOpenSettings = onOpenSettings)
        } else {
            MicSection(isListening = data.isListening, onMicClick = onMicClick, onStopListening = onStopListening)
        }

        data.result?.let { result ->
            ResultCard(result = result, onRetry = onMicClick)
        }

        if (data.sessionScores.isNotEmpty()) {
            val average = data.sessionScores.average().roundToInt()
            Text(text = "Session average: $average% (${data.sessionScores.size} tries)")
        }
    }
}

@Composable
private fun BookPicker(books: List<Book>, selectedBookId: String?, onSelectBook: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(books, key = { it.id }) { book ->
            FilterChip(
                selected = book.id == selectedBookId,
                onClick = { onSelectBook(book.id) },
                label = { Text(book.title) },
            )
        }
    }
}

@Composable
private fun SentenceNavigator(
    sentences: List<ReadingSentence>,
    selectedIndex: Int,
    onSelectSentence: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onSelectSentence(selectedIndex - 1) }, enabled = selectedIndex > 0) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous sentence")
            }
            Text(text = "${selectedIndex + 1} / ${sentences.size}")
            IconButton(
                onClick = { onSelectSentence(selectedIndex + 1) },
                enabled = selectedIndex < sentences.lastIndex,
            ) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next sentence")
            }
        }
        Text(text = sentences[selectedIndex].text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun PermissionNotice(onRequestPermission: () -> Unit, onOpenSettings: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Microphone permission is required to practice speaking.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRequestPermission) { Text("Allow microphone") }
            OutlinedButton(onClick = onOpenSettings) { Text("Open settings") }
        }
    }
}

@Composable
private fun MicSection(isListening: Boolean, onMicClick: () -> Unit, onStopListening: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = if (isListening) onStopListening else onMicClick) {
            Icon(
                if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                contentDescription = if (isListening) "Stop listening" else "Start listening",
                tint = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
        if (isListening) {
            Text(text = "Listening...")
        }
    }
}

@Composable
private fun ResultCard(result: PronunciationResult, onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Accuracy: ${result.accuracy}% (reference score, not phoneme-level)",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(text = "You said: \"${result.recognizedText}\"")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            result.wordResults.forEach { word ->
                WordChip(word)
            }
        }
        Button(onClick = onRetry) { Text("Try again") }
    }
}

@Composable
private fun WordChip(word: WordMatchResult) {
    Text(
        text = word.word,
        color = if (word.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Preview(showBackground = true)
@Composable
private fun PronunciationScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                PronunciationData(
                    books = listOf(Book("bundled:my_pet_dog.txt", "my pet dog", BookSource.BUNDLED, "my_pet_dog.txt")),
                    selectedBook = Book("bundled:my_pet_dog.txt", "my pet dog", BookSource.BUNDLED, "my_pet_dog.txt"),
                    sentences = listOf(
                        ReadingSentence(0, "I have a pet dog."),
                        ReadingSentence(1, "His name is Max."),
                    ),
                    selectedSentenceIndex = 0,
                    isListening = false,
                    isPlayingTts = false,
                    ttsRate = 1f,
                    isTtsRepeatEnabled = false,
                    result = PronunciationResult(
                        recognizedText = "I have a big dog",
                        accuracy = 75,
                        wordResults = listOf(
                            WordMatchResult("I", true),
                            WordMatchResult("have", true),
                            WordMatchResult("a", true),
                            WordMatchResult("pet", false),
                            WordMatchResult("dog", true),
                        ),
                    ),
                    hasRecordPermission = true,
                    sessionScores = listOf(75, 80),
                ),
            ),
        ) { data ->
            PronunciationContent(
                data = data,
                onSelectBook = {},
                onSelectSentence = {},
                onListenToSentence = {},
                onStopTts = {},
                onTtsRateSelected = {},
                onToggleTtsRepeat = {},
                onMicClick = {},
                onStopListening = {},
                onOpenSettings = {},
            )
        }
    }
}
