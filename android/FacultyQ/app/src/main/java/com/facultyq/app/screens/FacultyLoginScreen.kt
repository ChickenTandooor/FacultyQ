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
fun FacultyLoginScreen(
    onBackClick: () -> Unit,
    onFacultySelected: (String) -> Unit
) {

    var facultyName by
    remember {
        mutableStateOf("")
    }

    val context =
        LocalContext.current

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
            onClick =
                onBackClick
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
            text = "Faculty",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Enter your faculty name to access your dashboard.",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
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
                Text(
                    "Example: Dr. Sharma"
                )
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Button(

            onClick = {

                val faculty =
                    FacultyQRepository
                        .faculties
                        .find {

                            it.name.equals(
                                facultyName.trim(),
                                ignoreCase = true
                            )
                        }

                if (faculty != null) {

                    onFacultySelected(
                        faculty.id
                    )

                } else {

                    Toast.makeText(
                        context,
                        "Faculty account not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            enabled =
                facultyName.isNotBlank(),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Continue")
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(
            text =
                "Only your own faculty dashboard will be shown here.",
            style =
                MaterialTheme.typography.bodySmall
        )
    }
}