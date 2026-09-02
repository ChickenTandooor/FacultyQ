package com.facultyq.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.Faculty
import com.facultyq.app.data.FacultyStatus

@Composable
fun FacultyResultCard(
    faculty: Faculty,
    queueSize: Int,
    onApplyClick: () -> Unit
) {

    val statusText =
        when (faculty.status) {

            FacultyStatus.AVAILABLE ->
                "🟢 Available"

            FacultyStatus.BUSY ->
                "🟡 Busy"

            FacultyStatus.DO_NOT_DISTURB ->
                "🔴 Do Not Disturb"

            FacultyStatus.AWAY ->
                "⚫ Away"
        }

    val canJoin =
        faculty.isAvailableToday &&
                faculty.status !=
                FacultyStatus.DO_NOT_DISTURB &&
                faculty.status !=
                FacultyStatus.AWAY &&
                queueSize <
                faculty.queueCapacity

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text = faculty.name,
                style =
                    MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Cabin: ${faculty.cabin}"
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = statusText
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Queue: $queueSize / ${faculty.queueCapacity}",
                style =
                    MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                onClick =
                    onApplyClick,

                enabled =
                    canJoin,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    if (canJoin)
                        "Apply for Queue"
                    else
                        "Currently Unavailable"
                )
            }
        }
    }
}