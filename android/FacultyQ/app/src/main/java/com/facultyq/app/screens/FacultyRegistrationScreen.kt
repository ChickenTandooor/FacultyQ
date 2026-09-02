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
fun FacultyRegistrationScreen(
    onBackClick: () -> Unit,
    onSubmitted: () -> Unit
) {

    var facultyName by
    remember { mutableStateOf("") }

    var idCardScanned by
    remember { mutableStateOf(false) }

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
            text = "Faculty Registration",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Submit your details for admin verification."
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

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        OutlinedButton(
            onClick = {

                idCardScanned = true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(

                if (idCardScanned)
                    "✓ University ID Card Scanned"
                else
                    "Scan University ID Card"
            )
        }

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                if (idCardScanned)
                    "ID card submitted for verification."
                else
                    "Use your university ID card for verification.",
            style =
                MaterialTheme.typography.bodySmall
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Button(
            onClick = {

                FacultyQRepository
                    .addPendingFaculty(
                        facultyName
                    )

                Toast.makeText(
                    context,
                    "Registration submitted",
                    Toast.LENGTH_SHORT
                ).show()

                onSubmitted()
            },

            enabled =
                facultyName.isNotBlank() &&
                        idCardScanned,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Submit Registration")
        }
    }
}