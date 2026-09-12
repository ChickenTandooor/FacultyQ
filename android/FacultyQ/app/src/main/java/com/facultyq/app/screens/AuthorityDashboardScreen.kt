package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.Authority
import com.facultyq.app.data.FacultyQRepository

@Composable
fun AuthorityDashboardScreen(
    authorityId: String,
    onBackClick: () -> Unit
) {
    val authority = FacultyQRepository.getAuthority(authorityId)

    if (authority == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Authority not found",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(onClick = onBackClick) {
                Text("Back")
            }
        }

        return
    }

    val queue = FacultyQRepository.getQueueForAuthority(authorityId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Authority Dashboard",
            style = MaterialTheme.typography.headlineSmall
        )

        AuthorityHeader(
            authority = authority
        )

        Text(
            text = "Availability",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if (authority.isAvailableToday) {
                    "Available today"
                } else {
                    "Not available today"
                }
            )

            Switch(
                checked = authority.isAvailableToday,
                onCheckedChange = {
                    FacultyQRepository.updateAuthorityAvailability(
                        authorityId,
                        it
                    )
                }
            )
        }

        Text(
            text = "Queue Status",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "${queue.size} / ${authority.queueCapacity} students waiting",
            style = MaterialTheme.typography.bodyMedium
        )

        if (queue.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No students are currently waiting.",
                    modifier = Modifier.padding(16.dp)
                )
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(queue) { entry ->

                    AuthorityQueueStudentCard(
                        entryId = entry.id,
                        position = entry.position,
                        enrollmentNumber = entry.studentEnrollmentNumber,
                        currentClass = entry.studentCurrentClass,
                        purpose = entry.purpose
                    )
                }
            }
        }

        TextButton(
            onClick = onBackClick
        ) {
            Text("Back")
        }
    }
}


@Composable
private fun AuthorityHeader(
    authority: Authority
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = authority.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "${authority.role.name} · ${authority.department}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Cabin ${authority.cabin}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = authority.status.name.replace("_", " "),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
private fun AuthorityQueueStudentCard(
    entryId: String,
    position: Int,
    enrollmentNumber: String,
    currentClass: String,
    purpose: String
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "#$position  $enrollmentNumber",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = currentClass,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Purpose: $purpose",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        FacultyQRepository.serveQueueEntry(
                            entryId
                        )
                    }
                ) {
                    Text("Serve")
                }

                OutlinedButton(
                    onClick = {
                        FacultyQRepository.completeQueueEntry(
                            entryId
                        )
                    }
                ) {
                    Text("Complete")
                }
            }
        }
    }
}