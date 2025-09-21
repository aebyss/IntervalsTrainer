package com.example.fretboardlearner.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import com.example.fretboardlearner.data.model.PitchToNoteConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioInputViewModel : ViewModel() {

    // A simple signal that becomes true when we find the note.
    private val _isCorrectNoteHeard = MutableStateFlow(false)
    val isCorrectNoteHeard: StateFlow<Boolean> = _isCorrectNoteHeard

    private var dispatcher: AudioDispatcher? = null
    private val sampleRate = 44100
    private val bufferSize = 2048
    private val bufferOverlap = 0

    // The note we are specifically listening for.
    private var targetNote: String? = null
    private var stableNoteCounter = 0
    private val CONFIDENCE_THRESHOLD = 5 // Keep our stability check

    /**
     * This is the new way to start listening. We give it a mission.
     */
    fun listenFor(note: String) {
        targetNote = note
        _isCorrectNoteHeard.value = false // Reset the signal
        stableNoteCounter = 0
        startDispatcher() // Start the underlying audio processing
    }

    private fun startDispatcher() {
        if (dispatcher == null) {
            try {
                dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, bufferOverlap)
                val pdh = PitchDetectionHandler { result, _ ->
                    val pitchInHz = result.pitch
                    if (pitchInHz != -1f) {
                        val currentNote = PitchToNoteConverter.frequencyToNoteName(pitchInHz)

                        // *** THE NEW CORE LOGIC ***
                        // Only do something if the note we heard is the one we're looking for.
                        if (currentNote == targetNote) {
                            stableNoteCounter++ // Increase our confidence
                        } else {
                            stableNoteCounter = 0 // Wrong note, reset confidence
                        }

                        // If we are confident we heard the correct note...
                        if (stableNoteCounter >= CONFIDENCE_THRESHOLD) {
                            _isCorrectNoteHeard.value = true // FIRE THE SIGNAL!
                            stopListening() // Stop listening to save battery
                        }
                    } else {
                        stableNoteCounter = 0 // Silence, reset confidence
                    }
                }

                val pitchProcessor = PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.YIN, sampleRate.toFloat(), bufferSize, pdh
                )
                dispatcher?.addAudioProcessor(pitchProcessor)
                Thread(dispatcher, "Audio Dispatcher").start()

            } catch (e: Exception) {
                Log.e("AudioInputViewModel", "Error initializing audio dispatcher: ${e.message}")
            }
        }
    }

    fun stopListening() {
        if (dispatcher?.isStopped == false) {
            dispatcher?.stop()
        }
        dispatcher = null
        targetNote = null
        stableNoteCounter = 0
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}