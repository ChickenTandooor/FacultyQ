package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.Authority
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.data.Student
import com.facultyq.app.network.AddStudentRequest
import com.facultyq.app.network.JoinAuthorityQueueRequest
import com.facultyq.app.network.NetworkModule
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun AuthorityQueueDetailsScreen(
    authorityId: String,
    enrollmentNumber: String,
    onJoined: (String) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var authority by remember { mutableStateOf<Authority?>(null) }
    var student by remember { mutableStateOf<Student?>(null) }

    var purpose by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var joining by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    /*
     * Load authority and student information from the backend.
     */
    LaunchedEffect(authorityId, enrollmentNumber) {

        try {
            authority =
                FacultyQRepository.fetchAuthorityFromBackend(authorityId)

            /*
             * First try PostgreSQL.
             */
            try {

                val backendStudent =
                    NetworkModule.api.getStudent(enrollmentNumber)

                student = Student(
                    enrollmentNumber =
                        backendStudent.enrollment_number,
                    name =
                        backendStudent.name,
                    currentClass =
                        backendStudent.current_class
                )

            } catch (e: HttpException) {

                /*
                 * If the student is not yet in PostgreSQL,
                 * fall back to the locally saved student.
                 */
                if (e.code() == 404) {

                    val localStudent =
                        FacultyQRepository.getStudent(enrollmentNumber)

                    if (localStudent != null) {
                        student = localStudent
                    } else {
                        errorMessage =
                            "Student information is unavailable."
                    }

                } else {
                    throw e
                }
            }

        } catch (e: Exception) {

            errorMessage =
                e.message ?: "Unable to load information."

        } finally {
            loading = false
        }
    }

    /*
     * Loading screen
     */
    if (loading) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            CircularProgressIndicator()

            Text(
                text = "Loading...",
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        return
    }

    val selectedAuthority = authority

    /*
     * Authority could not be loaded.
     */
    if (selectedAuthority == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Authority not found",
                style = MaterialTheme.typography.headlineSmall
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        return
    }

    /*
     * Main screen
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = selectedAuthority.name,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text =
                "${selectedAuthority.role.name} • " +
                        selectedAuthority.department
        )

        Text(
            text =
                "Cabin: ${selectedAuthority.cabin}"
        )

        Text(
            text =
                "Status: ${selectedAuthority.status.name}"
        )

        Text(
            text =
                "Queue capacity: " +
                        selectedAuthority.queueCapacity
        )

        /*
         * Student information
         */
        student?.let { currentStudent ->

            Text(
                text =
                    "Student: ${currentStudent.name}"
            )

            Text(
                text =
                    "Enrollment: " +
                            currentStudent.enrollmentNumber
            )

            Text(
                text =
                    "Class: ${currentStudent.currentClass}"
            )
        }

        /*
         * Purpose
         */
        OutlinedTextField(
            value = purpose,
            onValueChange = {
                purpose = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Purpose of visit")
            },
            placeholder = {
                Text("Example: Signature on document")
            },
            minLines = 3
        )

        /*
         * Error
         */
        errorMessage?.let { message ->

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        /*
         * Join Queue
         */
        Button(
            onClick = {

                val currentStudent = student

                if (currentStudent == null) {
                    errorMessage =
                        "Student information is unavailable."
                    return@Button
                }

                if (purpose.isBlank()) {
                    errorMessage =
                        "Please enter the purpose of your visit."
                    return@Button
                }

                scope.launch {

                    joining = true
                    errorMessage = null

                    try {

                        /*
                         * Make sure the student exists in PostgreSQL.
                         */
                        try {

                            NetworkModule.api.getStudent(
                                currentStudent.enrollmentNumber
                            )

                        } catch (e: HttpException) {

                            if (e.code() == 404) {

                                NetworkModule.api.addStudent(
                                    AddStudentRequest(
                                        enrollment_number =
                                            currentStudent.enrollmentNumber,
                                        name =
                                            currentStudent.name,
                                        current_class =
                                            currentStudent.currentClass
                                    )
                                )

                            } else {
                                throw e
                            }
                        }

                        /*
                         * Check existing queue first.
                         */
                        val queueResponse =
                            NetworkModule.api.getAuthorityQueue(
                                authorityId
                            )

                        val existingEntry =
                            queueResponse.queue.firstOrNull {

                                it.student_enrollment_number ==
                                        currentStudent.enrollmentNumber &&

                                        (
                                                it.status == "WAITING" ||
                                                        it.status == "SERVING"
                                                )
                            }

                        /*
                         * Existing queue found.
                         */
                        if (existingEntry != null) {

                            onJoined(existingEntry.id)

                            return@launch
                        }

                        /*
                         * No existing queue.
                         * Create a new one.
                         */
                        val response =
                            NetworkModule.api.joinAuthorityQueue(
                                authorityId = authorityId,
                                request =
                                    JoinAuthorityQueueRequest(
                                        student_enrollment_number =
                                            currentStudent.enrollmentNumber,
                                        purpose =
                                            purpose.trim(),
                                        student_current_class =
                                            currentStudent.currentClass
                                    )
                            )

                        onJoined(
                            response.queue_entry.id
                        )

                    } catch (e: HttpException) {

                        /*
                         * Duplicate queue protection.
                         */
                        if (e.code() == 409) {

                            try {

                                val queueResponse =
                                    NetworkModule.api.getAuthorityQueue(
                                        authorityId
                                    )

                                val existingEntry =
                                    queueResponse.queue.firstOrNull {

                                        it.student_enrollment_number ==
                                                currentStudent.enrollmentNumber &&

                                                (
                                                        it.status == "WAITING" ||
                                                                it.status == "SERVING"
                                                        )
                                    }

                                if (existingEntry != null) {

                                    onJoined(
                                        existingEntry.id
                                    )

                                } else {

                                    errorMessage =
                                        "You are already in this authority's queue."
                                }

                            } catch (inner: Exception) {

                                errorMessage =
                                    "You are already in this authority's queue."
                            }

                        } else {

                            errorMessage =
                                "Backend error: HTTP ${e.code()}"
                        }

                    } catch (e: Exception) {

                        errorMessage =
                            e.message
                                ?: "Unable to join the queue."

                    } finally {

                        joining = false
                    }
                }
            },

            modifier = Modifier.fillMaxWidth(),

            enabled =
                !joining && student != null
        ) {

            Text(
                if (joining)
                    "Checking queue..."
                else
                    "Join Queue"
            )
        }

        /*
         * Back
         */
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !joining
        ) {
            Text("Back")
        }
    }
}