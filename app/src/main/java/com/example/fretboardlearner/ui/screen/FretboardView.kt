package com.example.fretboardlearner.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fretboardlearner.ui.viewmodel.IntervalQuestion

@OptIn(ExperimentalTextApi::class)
@Composable
fun FretboardView(
    question: IntervalQuestion,
    showTargetNote: Boolean,
    startFret: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier.fillMaxWidth().aspectRatio(2.5f)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val numFretsToShow = 4
            val numStrings = 6
            val fretWidth = size.width / numFretsToShow
            val stringSpacing = size.height / (numStrings + 1)

            // --- These functions are now defined below ---
            drawFocusedFretsAndStrings(startFret, fretWidth, stringSpacing, numStrings, numFretsToShow, textMeasurer)
            drawFocusedFretMarkers(startFret, fretWidth, size.height)

            drawNoteOnFretboard(question.rootPosition, fretWidth, stringSpacing, startFret, question.rootNote, textMeasurer, isRoot = true)
            if (showTargetNote) {
                drawNoteOnFretboard(question.targetPosition, fretWidth, stringSpacing, startFret, question.targetNote, textMeasurer, isRoot = false)
            }
        }
    }
}

// --- FIX: ADDING THE MISSING HELPER FUNCTIONS BACK ---

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawFocusedFretsAndStrings(startFret: Int, fretWidth: Float, stringSpacing: Float, numStrings: Int, numFretsToShow: Int, textMeasurer: TextMeasurer) {
    for (i in 0..numFretsToShow) {
        val x = i * fretWidth
        val strokeWidth = if (i == 0 && startFret == 0) 8f else 2f
        val color = if (i == 0 && startFret == 0) Color.White else Color.Gray
        drawLine(color, Offset(x, stringSpacing), Offset(x, size.height - stringSpacing), strokeWidth = strokeWidth)
    }

    for (string in 1..numStrings) {
        val y = string * stringSpacing
        val strokeWidth = (numStrings - string + 1) / 2f + 1
        drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth = strokeWidth)
    }

    for (i in 0 until numFretsToShow) {
        val fretNumber = startFret + i
        if (fretNumber == 0) continue
        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(fretNumber.toString()),
            style = TextStyle(fontSize = 10.sp, color = Color.Gray)
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                (i * fretWidth) + (fretWidth / 2) - (textLayoutResult.size.width / 2),
                size.height - (stringSpacing / 2)
            )
        )
    }
}

private fun DrawScope.drawFocusedFretMarkers(startFret: Int, fretWidth: Float, height: Float) {
    val markers = listOf(3, 5, 7, 9, 12, 15)
    for (markerFret in markers) {
        if (markerFret >= startFret && markerFret < startFret + 4) {
            val relativeFret = markerFret - startFret
            val circleX = (relativeFret * fretWidth) + (fretWidth / 2)
            val radius = 6.dp.toPx()
            if (markerFret == 12 || markerFret == 24) {
                drawCircle(Color.DarkGray, radius = radius, center = Offset(circleX, height * 0.33f))
                drawCircle(Color.DarkGray, radius = radius, center = Offset(circleX, height * 0.66f))
            } else {
                drawCircle(Color.DarkGray, radius = radius, center = Offset(circleX, height / 2))
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawNoteOnFretboard(
    position: com.example.fretboardlearner.data.model.FretboardPosition,
    fretWidth: Float,
    stringSpacing: Float,
    startFret: Int,
    noteName: String,
    textMeasurer: TextMeasurer,
    isRoot: Boolean
) {
    val stringY = position.string * stringSpacing
    val relativeFret = position.fret - startFret
    if (relativeFret < 0 || relativeFret >= 4) return

    val fretX = (relativeFret * fretWidth) + (fretWidth / 2)
    val noteColor = if (isRoot) Color.Green else Color.Red
    val textColor = Color.Black
    val circleRadius = stringSpacing * 0.45f

    drawCircle(color = noteColor, radius = circleRadius, center = Offset(fretX, stringY))
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(noteName),
        style = TextStyle(fontSize = (circleRadius * 0.8f).toSp(), color = textColor)
    )
    drawText(
        textLayoutResult,
        topLeft = Offset(fretX - textLayoutResult.size.width / 2, stringY - textLayoutResult.size.height / 2)
    )
}