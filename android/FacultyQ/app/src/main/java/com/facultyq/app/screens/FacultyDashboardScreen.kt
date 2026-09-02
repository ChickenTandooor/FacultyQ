package com.facultyq.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.data.FacultyStatus

@Composable
fun FacultyDashboardScreen(
    facultyId: String,
    onBackClick: () -> Unit
) {

    val faculty =
        FacultyQRepository
            .getFaculty(facultyId)

    var capacityText by
    remember(faculty?.queueCapacity) {
        mutableStateOf(
            faculty?.queueCapacity
                ?.toString()
                ?: "5"
        )
    }

    BackHandler {
        onBackClick()
    }

    if (faculty == null) {

        Text(
            text = "Faculty not found"
        )

        return
    }

    val queue =
        FacultyQRepository
            .getQueueForFaculty(facultyId)

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
                Modifier.height(12.dp)
        )

        Text(
            text = "Faculty Dashboard",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text = faculty.name,
            style =
                MaterialTheme.typography.titleLarge
        )

        Text(
            text =
                "Cabin: ${faculty.cabin}"
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text = "Status",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        FacultyStatus.entries
            .forEach { status ->

                OutlinedButton(

                    onClick = {

                        FacultyQRepository
                            .updateFacultyStatus(
                                facultyId,
                                status
                            )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 6.dp
                            )
                ) {

                    Text(

                        when (status) {

                            FacultyStatus.AVAILABLE ->
                                "🟢 Available"

                            FacultyStatus.BUSY ->
                                "🟡 Busy"

                            FacultyStatus.DO_NOT_DISTURB ->
                                "🔴 Do Not Disturb"

                            FacultyStatus.AWAY ->
                                "⚫ Away"
                        }
                    )
                }
            }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text = "Available today"
                )

                Text(
                    text =
                        if (faculty.isAvailableToday)
                            "Students can join"
                        else
                            "Not accepting today",
                    style =
                        MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked =
                    faculty.isAvailableToday,

                onCheckedChange = {

                    FacultyQRepository
                        .updateFacultyAvailability(
                            facultyId,
                            it
                        )
                }
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutlinedTextField(
            value =
                capacityText,

            onValueChange = {

                capacityText =
                    it.filter { char ->
                        char.isDigit()
                    }
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Queue capacity")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Button(
            onClick = {

                val capacity =
                    capacityText.toIntOrNull()

                if (
                    capacity != null &&
                    capacity > 0
                ) {

                    FacultyQRepository
                        .updateQueueCapacity(
                            facultyId,
                            capacity
                        )
                }
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Save Capacity")
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text =
                "Current Queue (${queue.size} / ${faculty.queueCapacity})",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        if (queue.isEmpty()) {

            Text(
                text =
                    "No students waiting."
            )

        } else {

            queue.forEach { entry ->

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 10.dp
                            )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp)
                    ) {

                        Text(
                            text =
                                "#${entry.position}  ${entry.studentEnrollmentNumber}",
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Purpose: ${entry.purpose}"
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Class: ${entry.studentCurrentClass}"
                        )
                    }
                }
            }
        }
    }
}