
package com.facultyq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.navigation.AppNavigation
import com.facultyq.app.network.NetworkModule
import com.facultyq.app.ui.theme.FacultyQTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacultyQRepository.initialize(applicationContext)

        setContent {
            var backendReady by remember { mutableStateOf(false) }
            var checkingBackend by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                checkingBackend = true
                backendReady = NetworkModule.initialize()
                checkingBackend = false
            }

            FacultyQTheme {
                when {
                    checkingBackend -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Text("Connecting to FacultyQ server...")
                        }
                    }

                    backendReady -> {
                        AppNavigation()
                    }

                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Unable to connect to FacultyQ server.",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Check that your PC and device are on the same Wi-Fi.")
                        }
                    }
                }
            }
        }
    }
}
