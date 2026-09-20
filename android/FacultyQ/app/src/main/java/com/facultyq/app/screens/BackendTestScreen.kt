package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.network.NetworkModule
import kotlinx.coroutines.launch

@Composable
fun BackendTestScreen() {

    var result by remember {
        mutableStateOf("Press the button to test the backend")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("FacultyQ Backend Test")

        Button(
            onClick = {

                loading = true

                scope.launch {

                    try {

                        val response =
                            NetworkModule.api.getAuthorities()

                        result =
                            "Connected successfully!\n\n" +
                                    "Authorities: ${response.count}\n\n" +
                                    response.authorities.joinToString(
                                        separator = "\n\n"
                                    ) {
                                        "${it.name}\n" +
                                                "${it.role} · ${it.department}\n" +
                                                "Cabin ${it.cabin}\n" +
                                                "Status: ${it.status}"
                                    }

                    } catch (e: Exception) {

                        result =
                            "Connection failed:\n\n" +
                                    "${e.javaClass.simpleName}\n" +
                                    "${e.message}"
                    }

                    loading = false
                }
            }
        ) {
            Text("Test Backend")
        }

        if (loading) {

            CircularProgressIndicator()

        } else {

            Text(result)
        }
    }
}