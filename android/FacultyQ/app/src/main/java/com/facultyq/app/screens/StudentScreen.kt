
package com.facultyq.app.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.facultyq.app.R
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.data.Student

@Composable
fun StudentScreen(
    onBackClick: () -> Unit,
    onContinueClick: (String) -> Unit
) {
    val context = LocalContext.current

    var enrollmentNumber by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var currentClass by remember { mutableStateOf("") }

    BackHandler {
        onBackClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1F3A))
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.campus_background
            ),
            contentDescription = "College campus background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x770B1F3A),
                            Color(0xAA0B1F3A),
                            Color(0xE60B1F3A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "FacultyQ",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome, Student",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your details once. FacultyQ will remember them on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE6EDF7)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xF7FFFFFF))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Student details",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0B1F3A)
                )

                OutlinedTextField(
                    value = enrollmentNumber,
                    onValueChange = { enrollmentNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enrollment Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = currentClass,
                    onValueChange = { currentClass = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Current Class") },
                    placeholder = { Text("Example: CSE 5th Sem") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

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

                        val student = Student(
                            enrollmentNumber = enrollmentNumber.trim(),
                            name = studentName.trim(),
                            currentClass = currentClass.trim()
                        )

                        FacultyQRepository.saveStudent(
                            context,
                            student
                        )

                        onContinueClick(student.enrollmentNumber)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0B1F3A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Save & Continue")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your campus. Your queue. Less waiting.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE6EDF7)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
