package com.example.fretboardlearner.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fretboardlearner.data.model.FretboardPosition
import com.example.fretboardlearner.data.model.Interval
import com.example.fretboardlearner.data.model.MusicTheory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

// Removed DisplayMode, as we will only use the fretboard now.
enum class IntervalDisplayMode { SYMBOL, ROMAN }

data class IntervalQuestion(
    val rootNote: String,
    val targetNote: String,
    val interval: Interval,
    val rootPosition: FretboardPosition,
    val targetPosition: FretboardPosition
)

data class FretboardTrainerUiState(
    val questionId: Int = 0,
    val currentRootNote: String = "C", // The user's selected ROOT NOTE
    val currentPositionIndex: Int = 0, // 0-11 for frets 1-12
    val intervalDisplayMode: IntervalDisplayMode = IntervalDisplayMode.SYMBOL,
    val currentQuestion: IntervalQuestion? = null,
    val feedbackMessage: String? = null,
    val isQuestionActive: Boolean = true
)

class FretboardTrainerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FretboardTrainerUiState())
    val uiState: StateFlow<FretboardTrainerUiState> = _uiState.asStateFlow()

    // --- SIMPLIFIED: Only standard, non-accidental notes ---
    val selectableRootNotes = listOf("C", "G", "D", "A", "E", "B", "F")

    // --- SIMPLIFIED: Positions are now just frets 1-12 ---
    val positionCount = 12

    init {
        generateNewQuestion()
    }

    fun setRootNote(newRoot: String) {
        _uiState.update { it.copy(currentRootNote = newRoot) }
        generateNewQuestion()
    }

    fun setPosition(positionIndex: Int) {
        _uiState.update { it.copy(currentPositionIndex = positionIndex) }
        generateNewQuestion()
    }

    fun setIntervalDisplayMode(mode: IntervalDisplayMode) {
        _uiState.update { it.copy(intervalDisplayMode = mode) }
    }

    fun markAsCorrect() {
        if (!_uiState.value.isQuestionActive) return
        _uiState.update {
            it.copy(
                isQuestionActive = false,
                feedbackMessage = "Correct!"
            )
        }
    }

    fun generateNewQuestion() {
        val rootNote = _uiState.value.currentRootNote
        val startFret = _uiState.value.currentPositionIndex
        val randomInterval = MusicTheory.INTERVALS.random()

        // 1. Find all possible positions for the root note within our 4-fret window.
        val rootPositionsInWindow = MusicTheory.FRETBOARD_MAP[rootNote]?.filter {
            it.fret >= startFret && it.fret < startFret + 4
        }
        // If there's no root note in this position, we can't make a question.
        if (rootPositionsInWindow.isNullOrEmpty()) {
            // In a real app, you might show a message like "No C in this position"
            return
        }
        val rootPosition = rootPositionsInWindow.first() // Pick the first available one

        // 2. Calculate the target note from the chosen root and random interval.
        val targetNote = MusicTheory.findNoteForInterval(rootNote, randomInterval)

        // 3. Find the closest physical position for the target note.
        val targetPositions = MusicTheory.FRETBOARD_MAP[targetNote] ?: return
        val targetPosition = targetPositions.minByOrNull {
            abs(it.string - rootPosition.string) + abs(it.fret - rootPosition.fret)
        } ?: targetPositions.random()

        val newQuestion = IntervalQuestion(
            rootNote = rootNote,
            targetNote = targetNote,
            interval = randomInterval,
            rootPosition = rootPosition,
            targetPosition = targetPosition
        )

        _uiState.update {
            it.copy(
                questionId = it.questionId + 1,
                currentQuestion = newQuestion,
                isQuestionActive = true,
                feedbackMessage = null
            )
        }
    }
}