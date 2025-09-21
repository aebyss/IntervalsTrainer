package com.example.fretboardlearner.data.model

// Data class to hold all the info about an interval
data class Interval(val name: String, val symbol: String, val roman: String, val semitones: Int)

// Data class to represent a musical key
data class Key(val name: String, val notes: List<String>) {
    // This makes it display nicely in the dropdown menu
    override fun toString(): String = name
}
data class FretboardPosition(val string: Int, val fret: Int) // String 1 is high E, 6 is low E


object MusicTheory {
    val CHROMATIC_SCALE = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    // In MusicTheory.kt
// This simplified list will be used to generate questions.
    val INTERVALS = listOf(
        Interval("Minor 2nd", "m2", "ii", 1),
        Interval("Major 2nd", "M2", "II", 2),
        Interval("Minor 3rd", "m3", "iii", 3),
        Interval("Major 3rd", "M3", "III", 4),
        Interval("Perfect 4th", "P4", "IV", 5),
        Interval("Perfect 5th", "P5", "V", 7) // Simplified to the most common intervals
    )

    // Scale patterns in semitones from the root
    private val MAJOR_SCALE_PATTERN = listOf(0, 2, 4, 5, 7, 9, 11)
    private val NATURAL_MINOR_SCALE_PATTERN = listOf(0, 2, 3, 5, 7, 8, 10)

    val FRETBOARD_MAP: Map<String, List<FretboardPosition>>
    private val STANDARD_TUNING = listOf("E", "B", "G", "D", "A", "E")
    init {
        val map = mutableMapOf<String, MutableList<FretboardPosition>>()
        for (stringIndex in 0 until 6) { // 0-5 for strings 1-6
            val openStringNote = STANDARD_TUNING[stringIndex]
            val openNoteIndex = CHROMATIC_SCALE.indexOf(openStringNote)
            for (fret in 0..15) {
                val noteIndex = (openNoteIndex + fret) % CHROMATIC_SCALE.size
                val noteName = CHROMATIC_SCALE[noteIndex]
                val position = FretboardPosition(string = stringIndex + 1, fret = fret)

                if (!map.containsKey(noteName)) {
                    map[noteName] = mutableListOf()
                }
                map[noteName]?.add(position)
            }
        }
        FRETBOARD_MAP = map
    }
    /**
     * Generates all the notes for a given key.
     * Example: generateKey("G", "Major") -> Key("G Major", ["G", "A", "B", "C", "D", "E", "F#"])
     */
    fun generateKey(rootNote: String, scaleType: String): Key {
        val pattern = if (scaleType == "Major") MAJOR_SCALE_PATTERN else NATURAL_MINOR_SCALE_PATTERN
        val rootIndex = CHROMATIC_SCALE.indexOf(rootNote)
        if (rootIndex == -1) return Key("$rootNote $scaleType", emptyList())

        val keyNotes = pattern.map { interval ->
            CHROMATIC_SCALE[(rootIndex + interval) % CHROMATIC_SCALE.size]
        }
        return Key("$rootNote $scaleType", keyNotes)
    }

    // This function is no longer used for question generation, but we keep it for reference
    fun findNoteForInterval(rootNote: String, interval: Interval): String {
        val rootIndex = CHROMATIC_SCALE.indexOf(rootNote)
        if (rootIndex == -1) return rootNote
        val targetIndex = (rootIndex + interval.semitones) % CHROMATIC_SCALE.size
        return CHROMATIC_SCALE[targetIndex]
    }
    /**
     * Calculates the starting fret for a given position of a key.
     * This is a simplified model based on the root on the 6th string.
     */
    fun getPositionStartFret(key: Key, positionIndex: Int): Int {
        val rootNoteOn6thString = FRETBOARD_MAP[key.notes[0]]?.find { it.string == 6 }
        val anchorFret = rootNoteOn6thString?.fret ?: 0

        val positionStartFrets = listOf(
            anchorFret - 2, // Position 1
            anchorFret,     // Position 2
            anchorFret + 2, // Position 3
            anchorFret + 5, // Position 4
            anchorFret + 7  // Position 5
        ).map { if (it < 1) 0 else it } // Ensure start fret is not negative, defaulting to open position

        return positionStartFrets.getOrElse(positionIndex) { 0 }
    }
    /**
     * Finds all notes of a given key that fall within a specific fretboard position.
     * Positions are anchored to the root note on the 6th string (low E).
     */
    fun generateScaleInPosition(key: Key, positionIndex: Int): List<FretboardPosition> {
        // 1. Find the first occurrence of the key's root note on the 6th string. This is our anchor.
        val rootNoteOn6thString = FRETBOARD_MAP[key.notes[0]]?.find { it.string == 6 }
        val anchorFret = rootNoteOn6thString?.fret ?: 0

        // 2. Define the starting fret for each of the 5 positions relative to the anchor.
        // This is a simplified model. A more advanced one could map to CAGED shapes.
        val positionStartFrets = listOf(
            anchorFret - 2, // Position 1
            anchorFret,     // Position 2
            anchorFret + 2, // Position 3
            anchorFret + 5, // Position 4
            anchorFret + 7  // Position 5
        ).map { if (it < 0) it + 12 else it } // Wrap around for keys near the nut

        val startFret = positionStartFrets.getOrElse(positionIndex) { 0 }
        // A position is typically a 4 or 5 fret box.
        val endFret = startFret + 4

        // 3. Get all positions for all notes in the key.
        val allPositionsInKey = key.notes.flatMap { note -> FRETBOARD_MAP[note] ?: emptyList() }

        // 4. Filter those positions to only include notes within our fret window.
        return allPositionsInKey.filter {
            // Handle open strings (fret 0) correctly within the first few positions
            if (it.fret == 0) {
                startFret <= 2 // Only include open strings for positions near the nut
            } else {
                it.fret >= startFret && it.fret <= endFret
            }
        }
    }

    private val noteToStaffPosition = mapOf(
        "C" to 5.5f, "D" to 4.5f, "E" to 3.5f, "F" to 2.5f,
        "G" to 1.5f, "A" to 0.5f, "B" to -0.5f
    )

    fun getStaffPosition(note: String): Float {
        val baseNote = note.first().toString()
        return noteToStaffPosition[baseNote] ?: 0f
    }
}