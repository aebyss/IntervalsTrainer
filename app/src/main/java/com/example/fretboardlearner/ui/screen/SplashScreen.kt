package com.example.fretboardlearner.ui.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    // This is a "callback" function. We will call it when our timer is finished.
    onTimeout: () -> Unit
) {
    // This effect will run exactly once when the SplashScreen is first displayed.
    LaunchedEffect(key1 = true) {
        // Wait for 2 seconds (2000 milliseconds)
        delay(2000L)
        // After the delay, call the onTimeout function to navigate away.
        onTimeout()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fretboard Learner",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}