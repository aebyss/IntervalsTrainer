package com.example.fretboardlearner.data.model

object PitchToNoteConverter {
    private val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private const val A4_FREQUENCY = 440.0
    private const val A4_MIDI_NOTE = 69 // MIDI note number for A4

    fun frequencyToNoteName(frequency: Float): String? {
        if (frequency <= 0) return null // Or handle silence/no clear pitch

        // Convert frequency to MIDI note number (can be fractional)
        val midiNote = 12 * (Math.log(frequency / A4_FREQUENCY) / Math.log(2.0)) + A4_MIDI_NOTE

        // Round to the nearest MIDI note to get the semitone
        val roundedMidiNote = Math.round(midiNote).toInt()

        if (roundedMidiNote < 0 || roundedMidiNote > 127) { // Standard MIDI range
            return null // Outside typical musical range
        }

        val noteIndex = roundedMidiNote % 12
        // val octave = (roundedMidiNote / 12) - 1 // If you need octave info
        return noteNames[noteIndex]
    }
}