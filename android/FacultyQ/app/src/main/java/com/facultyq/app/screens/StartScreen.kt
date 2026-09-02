package com.facultyq.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.components.RoleCard

@Composable
fun StartScreen(
    onStudentClick: () -> Unit,
    onFacultyClick: () -> Unit,
    onAdminClick: () -> Unit,
    onFacultyRegisterClick: () -> Unit
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "FQ",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text = "FacultyQ",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text = "Virtual faculty queue",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(32.dp)
        )

        Text(
            text = "Skip the crowd.",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Meet your faculty without waiting in a physical line."
        )

        Spacer(
            modifier =
                Modifier.height(36.dp)
        )

        Text(
            text = "CONTINUE AS",
            style =
                MaterialTheme.typography.labelMedium
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        RoleCard(
            icon = Icons.Default.Person,
            title = "Student",
            description =
                "Search faculty and join a queue",
            onClick =
                onStudentClick
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        RoleCard(
            icon = Icons.Default.Person,
            title = "Faculty",
            description =
                "Manage your availability and queue",
            onClick =
                onFacultyClick
        )

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        TextButton(
            onClick =
                onFacultyRegisterClick
        ) {

            Text(
                "Faculty registration"
            )
        }

        TextButton(
            onClick =
                onAdminClick
        ) {

            Text(
                "Admin access"
            )
        }
    }
}