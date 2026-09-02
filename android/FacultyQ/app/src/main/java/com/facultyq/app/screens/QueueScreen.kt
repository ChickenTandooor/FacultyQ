package com.facultyq.app.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.FacultyQRepository

@Composable
fun QueueScreen(
    enrollmentNumber: String,
    facultyId: String,
    queueId: String,
    onBackClick: () -> Unit
) {

    val context =
        LocalContext.current

    val faculty =
        FacultyQRepository
            .getFaculty(facultyId)

    val queueEntry =
        FacultyQRepository.queueEntries
            .find {
                it.id == queueId
            }

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
                Modifier.height(20.dp)
        )

        Text(
            text = "You're in the queue",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Your request has been added to the faculty queue."
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(20.dp)
            ) {

                Text(
                    text =
                        faculty?.name ?: "Faculty",
                    style =
                        MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
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
                    text = "YOUR POSITION",
                    style =
                        MaterialTheme.typography.labelMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "${queueEntry?.position ?: "-"}",
                    style =
                        MaterialTheme.typography.displaySmall
                )

                Text(
                    text = "in the queue"
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text = "🟢 Queue active"
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text =
                        "Purpose: ${queueEntry?.purpose ?: "-"}"
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Class: ${queueEntry?.studentCurrentClass ?: "-"}"
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Enrollment: $enrollmentNumber",
                    style =
                        MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Button(
            onClick = {

                FacultyQRepository
                    .leaveQueue(queueId)

                Toast.makeText(
                    context,
                    "You left the queue",
                    Toast.LENGTH_SHORT
                ).show()

                onBackClick()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Leave Queue")
        }
    }
}