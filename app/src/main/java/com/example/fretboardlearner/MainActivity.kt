package com.example.fretboardlearner

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fretboardlearner.ui.screen.FretboardTrainerScreen
import com.example.fretboardlearner.ui.screen.SplashScreen
import com.example.fretboardlearner.ui.theme.FretboardLearnerTheme
import com.example.fretboardlearner.ui.viewmodel.AudioInputViewModel
import com.example.fretboardlearner.ui.viewmodel.FretboardTrainerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    // The ViewModels are still created here, as they are shared across the app.
    private val fretboardTrainerViewModel: FretboardTrainerViewModel by viewModels()
    private val audioInputViewModel: AudioInputViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FretboardLearnerTheme {
                // The permission handler now wraps our entire navigation system.
                AudioPermissionHandler {
                    // This function now contains our navigation logic
                    AppNavigationHost(
                        fretboardTrainerViewModel = fretboardTrainerViewModel,
                        audioInputViewModel = audioInputViewModel
                    )
                }
            }
        }
    }
}

// --- NEW: This composable manages the entire navigation graph of the app ---
@Composable
fun AppNavigationHost(
    fretboardTrainerViewModel: FretboardTrainerViewModel,
    audioInputViewModel: AudioInputViewModel
) {
    // Create a NavController to manage screen transitions.
    val navController = rememberNavController()

    // NavHost is the container for all of your app's screens.
    NavHost(
        navController = navController,
        // The "startDestination" is the first screen that will be shown.
        startDestination = "splash_screen"
    ) {
        // Define the splash screen
        composable("splash_screen") {
            SplashScreen(
                onTimeout = {
                    // When the splash screen's timer finishes, navigate to the main screen.
                    // popUpTo removes the splash screen from the back stack, so the user
                    // can't press the back button to go back to it.
                    navController.navigate("main_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            )
        }

        // Define the main game screen
        composable("main_screen") {
            FretboardTrainerScreen(
                fretboardViewModel = fretboardTrainerViewModel,
                audioViewModel = audioInputViewModel
            )
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