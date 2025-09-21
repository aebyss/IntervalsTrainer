package com.example.fretboardlearner.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.fretboardlearner.R
import com.example.fretboardlearner.data.model.MusicTheory
import com.example.fretboardlearner.ui.viewmodel.IntervalQuestion

@Composable
fun MusicStaff(question: IntervalQuestion, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(120.dp).fillMaxWidth()) {
        val staffLineColor = Color.White
        val lineSpacing = 16.dp

        // Draw the 5 staff lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val startY = (size.height / 2) - (lineSpacing.toPx() * 2)
            repeat(5) { lineIndex ->
                val y = startY + (lineIndex * lineSpacing.toPx())
                drawLine(
                    color = staffLineColor,
                    start = Offset(x = 0f, y = y),
                    end = Offset(x = canvasWidth, y = y),
                    strokeWidth = 2f
                )
            }
        }

        // Place the Treble Clef
        Icon(
            painter = painterResource(id = R.drawable.ic_treble_clef),
            contentDescription = "Treble Clef",
            modifier = Modifier.height(100.dp).align(Alignment.CenterStart).offset(x = 16.dp),
            tint = Color.White
        )

        // Place the notes on the staff
        Note(noteName = question.rootNote, position = 0.35f)
        Note(noteName = question.targetNote, position = 0.55f)
    }
}

@Composable
fun BoxScope.Note(noteName: String, position: Float) {
    val lineSpacing = 16.dp
    val noteSize = (lineSpacing * 1.5f)
    val staffCenterY = 0.dp // Center of the Box
    val topStaffLineY = staffCenterY - (lineSpacing * 2)

    val staffPosition = MusicTheory.getStaffPosition(noteName)
    val noteOffsetY = topStaffLineY + (staffPosition * lineSpacing)

    // Note Body
    Icon(
        painter = painterResource(id = R.drawable.ic_music_note),
        contentDescription = noteName,
        modifier = Modifier
            .size(noteSize)
            .align(Alignment.Center)
            .offset(
                x = (150 * position).dp, // Crude horizontal positioning
                y = noteOffsetY
            ),
        tint = Color.White
    )

    // Accidental (Sharp or Flat)
    if (noteName.contains("#")) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sharp),
            contentDescription = "Sharp",
            modifier = Modifier
                .size(noteSize * 1.5f) // Accidentals are often taller
                .align(Alignment.Center)
                .offset(x = (150 * position - 20).dp, y = noteOffsetY),
            tint = Color.White
        )
    }
    // Add similar logic for flats if your theory engine produces them
}