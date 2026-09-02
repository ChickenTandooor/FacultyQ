package com.facultyq.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.components.FacultyResultCard
import com.facultyq.app.data.Faculty
import com.facultyq.app.data.FacultyQRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FacultySearchScreen(
    enrollmentNumber: String,
    onBackClick: () -> Unit,
    onApplyClick: (String) -> Unit
) {

    var searchText by
    remember { mutableStateOf("") }

    var searchResults by
    remember {
        mutableStateOf<List<Faculty>>(
            emptyList()
        )
    }

    val currentDateTime =
        remember {

            SimpleDateFormat(
                "dd MMM yyyy  •  hh:mm a",
                Locale.getDefault()
            ).format(Date())
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
                Modifier.height(12.dp)
        )

        Text(
            text = "Find Faculty",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text = currentDateTime,
            style =
                MaterialTheme.typography.bodySmall
        )

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        Text(
            text =
                "Enrollment: $enrollmentNumber"
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        OutlinedTextField(
            value =
                searchText,

            onValueChange = {
                searchText = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Search faculty by name")
            },

            singleLine = true,

            trailingIcon = {

                Icon(
                    imageVector =
                        Icons.Default.Search,
                    contentDescription =
                        "Search"
                )
            }
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Button(
            onClick = {

                searchResults =
                    FacultyQRepository
                        .searchFaculty(
                            searchText
                        )
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Search")
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        if (
            searchResults.isEmpty() &&
            searchText.isNotBlank()
        ) {

            Text(
                text =
                    "No faculty found."
            )
        }

        searchResults.forEach { faculty ->

            val queueSize =
                FacultyQRepository
                    .getQueueForFaculty(
                        faculty.id
                    )
                    .size

            FacultyResultCard(

                faculty =
                    faculty,

                queueSize =
                    queueSize,

                onApplyClick = {

                    onApplyClick(
                        faculty.id
                    )
                }
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }
    }
}