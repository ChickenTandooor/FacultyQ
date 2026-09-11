package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.data.QueueEntryStatus

@Composable
fun AuthorityQueueScreen(
    enrollmentNumber: String,
    authorityId: String,
    queueId: String,
    onBackClick: () -> Unit
) {

    val authority =
        FacultyQRepository.getAuthority(
            authorityId
        )

    val queueEntry =
        FacultyQRepository.queueEntries.find {
            it.id == queueId
        }

    if (
        authority == null ||
        queueEntry == null
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Queue information unavailable",
                style =
                    MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = onBackClick
            ) {
                Text("Back")
            }
        }

        return
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Your Queue",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Text(
            text = authority.name,
            style =
                MaterialTheme.typography.titleLarge
        )

        Text(
            text =
                "${authority.role.name} · ${authority.department}",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text =
                "Cabin ${authority.cabin}",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Your position",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                queueEntry.position.toString(),
            style =
                MaterialTheme.typography.displaySmall
        )

        Text(
            text = "Purpose",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text = queueEntry.purpose,
            style =
                MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Enrollment",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text = enrollmentNumber,
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Queue status",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                queueEntry.status.name,
            style =
                MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {

                FacultyQRepository.leaveQueue(
                    queueId
                )

                onBackClick()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Leave Queue")
        }
    }
}