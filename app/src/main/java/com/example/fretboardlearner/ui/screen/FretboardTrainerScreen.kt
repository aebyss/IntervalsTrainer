package com.example.fretboardlearner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fretboardlearner.ui.viewmodel.AudioInputViewModel
import com.example.fretboardlearner.ui.viewmodel.FretboardTrainerViewModel
import kotlinx.coroutines.delay

@Composable
fun FretboardTrainerScreen(
    modifier: Modifier = Modifier,
    fretboardViewModel: FretboardTrainerViewModel,
    audioViewModel: AudioInputViewModel
) {
    val uiState by fretboardViewModel.uiState.collectAsStateWithLifecycle()
    val isCorrectNoteHeard by audioViewModel.isCorrectNoteHeard.collectAsStateWithLifecycle()
    val currentQuestion = uiState.currentQuestion

    LaunchedEffect(uiState.questionId) {
        if (currentQuestion != null && uiState.isQuestionActive) {
            audioViewModel.listenFor(currentQuestion.targetNote)
        }
    }
    LaunchedEffect(isCorrectNoteHeard) {
        if (isCorrectNoteHeard) {
            fretboardViewModel.markAsCorrect()
        }
    }
    LaunchedEffect(uiState.feedbackMessage) {
        if (uiState.feedbackMessage != null) {
            delay(1500L)
            fretboardViewModel.generateNewQuestion()
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RootNoteSelector(
                notes = fretboardViewModel.selectableRootNotes,
                selectedNote = uiState.currentRootNote,
                onNoteSelected = { fretboardViewModel.setRootNote(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PositionSelector(
                positionCount = fretboardViewModel.positionCount,
                selectedPosition = uiState.currentPositionIndex,
                onPositionSelected = { fretboardViewModel.setPosition(it) }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentQuestion != null) {
                    FretboardView(
                        question = currentQuestion,
                        showTargetNote = !uiState.isQuestionActive,
                        startFret = uiState.currentPositionIndex + 1
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (currentQuestion != null) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = buildAnnotatedString {
                            append("Play the ")
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(currentQuestion.interval.name)
                            }
                            append(" from ")
                            withStyle(style = SpanStyle(color = Color.Green)) {
                                append(currentQuestion.rootNote)
                            }
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.isQuestionActive && currentQuestion != null) {
                    Text("Listening...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                }

                val feedback = uiState.feedbackMessage
                if (feedback != null) {
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun RootNoteSelector(
    notes: List<String>,
    selectedNote: String,
    onNoteSelected: (String) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Select a Root Note", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(notes) { note ->
                Button(
                    onClick = { onNoteSelected(note) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (note == selectedNote)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(note)
                }
            }
        }
    }
}

@Composable
fun PositionSelector(
    positionCount: Int,
    selectedPosition: Int,
    onPositionSelected: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Select a Start Fret", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(count = positionCount) { index ->
                Button(
                    onClick = { onPositionSelected(index) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (index == selectedPosition)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Fret ${index + 1}")
                }
            }
        }
    }
}