package vn.io.ducnq.audiomaster

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import vn.io.ducnq.audiomaster.service.AudioService
import vn.io.ducnq.audiomaster.ui.theme.AudioMasterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission immediately when opening the app
        checkPermissions()

        // Start rendering UI using Jetpack Compose
        setContent {
            AudioMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call main UI function
                    AudioMasterApp(
                        onToggleService = { isRunning -> toggleAudioService(isRunning) }
                    )
                }
            }
        }
    }

    /**
     * Main application UI
     */
    @Composable
    fun AudioMasterApp(onToggleService: (Boolean) -> Unit) {
        // State variable: stores whether the service is running or stopped
        var isServiceRunning by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AudioMaster Engine",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(onClick = {
                // Trigger start/stop Service logic
                onToggleService(isServiceRunning)
                // Toggle state to update button text automatically
                isServiceRunning = !isServiceRunning
            }) {
                Text(text = if (isServiceRunning) "Stop Service" else "Start Service")
            }
        }
    }

    /**
     * @output Requests necessary runtime permissions (e.g., Notifications for Android 13+)
     */
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    /**
     * @param isCurrentlyRunning Current state of the Service
     * @output Sends Intent to start or stop the Foreground Service
     */
    private fun toggleAudioService(isCurrentlyRunning: Boolean) {
        val serviceIntent = Intent(this, AudioService::class.java)

        if (!isCurrentlyRunning) {
            serviceIntent.action = "ACTION_START_SERVICE"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } else {
            serviceIntent.action = "ACTION_STOP_SERVICE"
            startService(serviceIntent)
        }
    }
}