package com.facultyq.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.data.FacultyStatus

@Composable
fun AuthorityQueueDetailsScreen(
    enrollmentNumber: String,
    authorityId: String,
    onBackClick: () -> Unit,
    onJoined: (String) -> Unit
) {

    val context = LocalContext.current

    val authority =
        FacultyQRepository.getAuthority(
            authorityId
        )

    val student =
        FacultyQRepository.getStudent(
            enrollmentNumber
        )
            ?: FacultyQRepository.savedStudent.value

    var purpose by remember {
        mutableStateOf("")
    }

    if (authority == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Authority not found",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = onBackClick
            ) {
                Text("Back")
            }
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = authority.name,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = authority.role.name,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = authority.department,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Cabin ${authority.cabin}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = authority.status.name.replace(
                "_",
                " "
            ),
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                "Queue capacity: ${authority.queueCapacity}",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Your Details",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text =
                "Enrollment: $enrollmentNumber",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text =
                "Class: ${
                    student?.currentClass
                        ?: "Not available"
                }",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Purpose of Visit",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = purpose,
            onValueChange = {
                purpose = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Enter purpose")
            },
            minLines = 3
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

                val newEntry =
                    FacultyQRepository.joinAuthorityQueue(
                        enrollmentNumber =
                            enrollmentNumber,

                        authorityId =
                            authorityId,

                        purpose =
                            purpose.trim(),

                        studentCurrentClass =
                            student?.currentClass
                                ?: "Not specified"
                    )

                if (newEntry != null) {

                    onJoined(
                        newEntry.id
                    )

                } else {

                    Toast.makeText(
                        context,
                        "Unable to join the queue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Join Queue")
        }

        TextButton(
            onClick = onBackClick
        ) {
            Text("Back")
        }
    }
}