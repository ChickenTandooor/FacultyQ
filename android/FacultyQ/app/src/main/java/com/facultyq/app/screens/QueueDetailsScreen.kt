package com.facultyq.app.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.FacultyQRepository

@Composable
fun QueueDetailsScreen(
    enrollmentNumber: String,
    facultyId: String,
    onBackClick: () -> Unit,
    onJoined: (String) -> Unit
) {

    var purpose by
    remember { mutableStateOf("") }

    val context =
        LocalContext.current

    val faculty =
        FacultyQRepository
            .getFaculty(facultyId)

    val student =
        FacultyQRepository
            .getStudent(enrollmentNumber)

    BackHandler {
        onBackClick()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp)
    ) {

        IconButton(
            onClick = onBackClick
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,
                contentDescription =
                    "Back"
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(
            text = "Queue Request",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                faculty?.name ?: "Faculty",
            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Cabin: ${faculty?.cabin ?: "-"}"
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "Your current class",
            style =
                MaterialTheme.typography.labelMedium
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                student?.currentClass ?: "-"
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        OutlinedTextField(
            value =
                purpose,

            onValueChange = {
                purpose = it
            },

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),

            label = {
                Text("Purpose of visit")
            },

            placeholder = {
                Text(
                    "Example: Signature on project form"
                )
            }
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Button(
            onClick = {

                if (purpose.isBlank()) {

                    Toast.makeText(
                        context,
                        "Please enter the purpose of your visit",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                val entry =
                    FacultyQRepository.joinQueue(

                        enrollmentNumber =
                            enrollmentNumber,

                        facultyId =
                            facultyId,

                        purpose =
                            purpose.trim(),

                        studentCurrentClass =
                            student?.currentClass ?: ""
                    )

                if (entry != null) {

                    onJoined(entry.id)

                } else {

                    Toast.makeText(
                        context,
                        "Unable to join this queue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Join Queue")
        }
    }
}