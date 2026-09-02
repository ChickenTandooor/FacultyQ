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
import com.facultyq.app.data.Student
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun StudentScreen(
    onBackClick: () -> Unit,
    onContinueClick: (String) -> Unit
) {

    val context =
        LocalContext.current

    var enrollmentNumber by
    remember {
        mutableStateOf("")
    }

    var studentName by
    remember {
        mutableStateOf("")
    }

    var currentClass by
    remember {
        mutableStateOf("")
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
                Modifier.height(12.dp)
        )

        Text(
            text = "Student",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Enter your details once. FacultyQ will remember them on this device.",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        OutlinedTextField(

            value =
                enrollmentNumber,

            onValueChange = {
                enrollmentNumber = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text(
                    "Enrollment Number"
                )
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(

            value =
                studentName,

            onValueChange = {
                studentName = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Name")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(

            value =
                currentClass,

            onValueChange = {
                currentClass = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Current Class")
            },

            placeholder = {
                Text(
                    "Example: CSE 5th Sem"
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

                if (
                    enrollmentNumber.isBlank() ||
                    studentName.isBlank() ||
                    currentClass.isBlank()
                ) {

                    Toast.makeText(
                        context,
                        "Please complete all details",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                val student =
                    Student(

                        enrollmentNumber =
                            enrollmentNumber.trim(),

                        name =
                            studentName.trim(),

                        currentClass =
                            currentClass.trim()
                    )

                FacultyQRepository
                    .saveStudent(
                        context,
                        student
                    )

                onContinueClick(
                    student.enrollmentNumber
                )
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Save & Continue")
        }
    }
}