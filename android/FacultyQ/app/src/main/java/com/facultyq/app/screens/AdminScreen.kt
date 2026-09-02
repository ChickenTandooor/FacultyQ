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

@Composable
fun AdminScreen(
    onBackClick: () -> Unit
) {

    var facultyName by
    remember { mutableStateOf("") }

    var cabinNumber by
    remember { mutableStateOf("") }

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
                Modifier.height(12.dp)
        )

        Text(
            text = "Admin",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text = "Manage Faculty",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "Add Faculty Manually",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(
            value =
                facultyName,

            onValueChange = {
                facultyName = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Faculty Name")
            },

            placeholder = {
                Text("Example: Dr. Anil Kumar")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(
            value =
                cabinNumber,

            onValueChange = {
                cabinNumber = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Cabin Number")
            },

            placeholder = {
                Text("Example: N604")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Button(
            onClick = {

                if (
                    facultyName.isNotBlank() &&
                    cabinNumber.isNotBlank()
                ) {

                    FacultyQRepository.addFaculty(
                        name =
                            facultyName,

                        cabin =
                            cabinNumber
                    )

                    facultyName = ""
                    cabinNumber = ""
                }
            },

            enabled =
                facultyName.isNotBlank() &&
                        cabinNumber.isNotBlank(),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Add Faculty")
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        Text(
            text = "Faculty List",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        FacultyQRepository.faculties
            .toList()
            .forEach { faculty ->

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
                                faculty.name,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Cabin: ${faculty.cabin}"
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Queue capacity: ${faculty.queueCapacity}"
                        )
                    }
                }
            }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Text(
            text = "Pending Registrations",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        if (
            FacultyQRepository.pendingFaculty.isEmpty()
        ) {

            Text(
                text =
                    "No pending registrations."
            )

        } else {

            FacultyQRepository
                .pendingFaculty
                .toList()
                .forEach { name ->

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
                                text = name,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    "University ID card submitted."
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            Button(
                                onClick = {

                                    FacultyQRepository
                                        .verifyFaculty(
                                            name
                                        )
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    "Verify & Activate"
                                )
                            }
                        }
                    }
                }
        }
    }
}