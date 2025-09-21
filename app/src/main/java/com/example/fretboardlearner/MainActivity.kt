package com.example.fretboardlearner

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fretboardlearner.ui.screen.FretboardTrainerScreen
import com.example.fretboardlearner.ui.theme.FretboardLearnerTheme
import com.example.fretboardlearner.ui.viewmodel.FretboardTrainerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.example.fretboardlearner.ui.viewmodel.AudioInputViewModel

class MainActivity : ComponentActivity() {
    private val fretboardTrainerViewModel: FretboardTrainerViewModel by viewModels()
    private val audioInputViewModel: AudioInputViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FretboardLearnerTheme {
                AudioPermissionHandler {
                    FretboardTrainerScreen(
                        fretboardViewModel = fretboardTrainerViewModel,
                        audioViewModel = audioInputViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AudioPermissionHandler(
    onPermissionGranted: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    when (permissionState.status) {
        PermissionStatus.Granted -> {
            onPermissionGranted()
        }
        is PermissionStatus.Denied -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val textToShow = if ((permissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    "The microphone is important for this app. Please grant the permission."
                } else {
                    "Microphone permission is required to use the audio trainer. Please grant the permission."
                }
                Text(textToShow, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}