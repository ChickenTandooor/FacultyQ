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
import com.facultyq.app.network.QueueEntryDto
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

    var authority by remember {
        mutableStateOf<Authority?>(null)
    }

    var student by remember {
        mutableStateOf<Student?>(null)
    }

    var activeQueueEntry by remember {
        mutableStateOf<QueueEntryDto?>(null)
    }

    var completedQueueEntry by remember {
        mutableStateOf<QueueEntryDto?>(null)
    }

    var purpose by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var joining by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Load authority, student and queue history.
     */
    LaunchedEffect(
        authorityId,
        enrollmentNumber
    ) {

        loading = true
        errorMessage = null

        try {

            /*
             * Load authority from backend.
             */
            authority =
                FacultyQRepository.fetchAuthorityFromBackend(
                    authorityId
                )

            /*
             * Load student.
             */
            try {

                val backendStudent =
                    NetworkModule.api.getStudent(
                        enrollmentNumber
                    )

                student =
                    Student(
                        enrollmentNumber =
                            backendStudent.enrollment_number,
                        name =
                            backendStudent.name,
                        currentClass =
                            backendStudent.current_class
                    )

            } catch (e: HttpException) {

                if (e.code() == 404) {

                    val localStudent =
                        FacultyQRepository.getStudent(
                            enrollmentNumber
                        )

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

            /*
             * Load this student's complete queue history.
             */
            val history =
                NetworkModule.api.getStudentQueueHistory(
                    enrollmentNumber
                )

            /*
             * Only look at visits for this authority.
             */
            val authorityHistory =
                history.queue.filter {
                    it.target_id == authorityId
                }

            /*
             * Check for an active queue.
             */
            activeQueueEntry =
                authorityHistory.firstOrNull {
                    it.status == "WAITING" ||
                            it.status == "SERVING"
                }

            /*
             * Find the most recent completed visit.
             */
            completedQueueEntry =
                authorityHistory.firstOrNull {
                    it.status == "COMPLETED"
                }

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Unable to load queue information."

        } finally {

            loading = false
        }
    }

    /*
     * Loading screen.
     */
    if (loading) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement =
                Arrangement.Center
        ) {

            CircularProgressIndicator()

            Text(
                text = "Loading...",
                modifier =
                    Modifier.padding(top = 16.dp)
            )
        }

        return
    }

    val selectedAuthority = authority

    /*
     * Authority not found.
     */
    if (selectedAuthority == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Authority not found",
                style =
                    MaterialTheme.typography.headlineSmall
            )

            errorMessage?.let {

                Text(
                    text = it,
                    color =
                        MaterialTheme.colorScheme.error
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
     * Main screen.
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = selectedAuthority.name,
            style =
                MaterialTheme.typography.headlineSmall
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
         * Student information.
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
         * ----------------------------------------
         * ACTIVE QUEUE
         * ----------------------------------------
         */
        if (activeQueueEntry != null) {

            Text(
                text = "Current Queue",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    "You are already in this authority's queue."
            )

            Text(
                text =
                    "Position: " +
                            activeQueueEntry!!.position
            )

            Text(
                text =
                    "Purpose: " +
                            activeQueueEntry!!.purpose
            )

            Text(
                text =
                    "Status: " +
                            activeQueueEntry!!.status
            )

            Button(
                onClick = {
                    onJoined(
                        activeQueueEntry!!.id
                    )
                },
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text("View Current Queue")
            }
        }

        /*
         * ----------------------------------------
         * COMPLETED VISIT
         * ----------------------------------------
         */
        else if (completedQueueEntry != null) {

            Text(
                text = "Previous Visit",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    "Status: COMPLETED"
            )

            Text(
                text =
                    "Purpose: " +
                            completedQueueEntry!!.purpose
            )

            Text(
                text =
                    "Previous queue position: " +
                            completedQueueEntry!!.position
            )

            Text(
                text =
                    "Your previous visit has been completed."
            )

            /*
             * Purpose for a new visit.
             */
            OutlinedTextField(
                value = purpose,
                onValueChange = {
                    purpose = it
                    errorMessage = null
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Purpose of new visit")
                },
                placeholder = {
                    Text(
                        "Example: Signature on document"
                    )
                },
                minLines = 3
            )

            errorMessage?.let {

                Text(
                    text = it,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

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
                            "Please enter the purpose of your new visit."

                        return@Button
                    }

                    scope.launch {

                        joining = true
                        errorMessage = null

                        try {

                            /*
                             * Make sure student exists.
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
                             * Create new queue entry.
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

                            if (e.code() == 409) {

                                /*
                                 * Check whether another active
                                 * queue appeared.
                                 */
                                try {

                                    val queue =
                                        NetworkModule.api
                                            .getAuthorityQueue(
                                                authorityId
                                            )

                                    val existing =
                                        queue.queue.firstOrNull {

                                            it.student_enrollment_number ==
                                                    currentStudent.enrollmentNumber

                                        }

                                    if (existing != null) {

                                        onJoined(
                                            existing.id
                                        )

                                    } else {

                                        errorMessage =
                                            "Unable to create a new queue entry."
                                    }

                                } catch (inner: Exception) {

                                    errorMessage =
                                        "Unable to create a new queue entry."
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
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    !joining
            ) {

                Text(
                    if (joining)
                        "Joining..."
                    else
                        "Join New Queue"
                )
            }
        }

        /*
         * ----------------------------------------
         * NO PREVIOUS VISIT
         * ----------------------------------------
         */
        else {

            OutlinedTextField(
                value = purpose,
                onValueChange = {
                    purpose = it
                    errorMessage = null
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text("Purpose of visit")
                },
                placeholder = {
                    Text(
                        "Example: Signature on document"
                    )
                },
                minLines = 3
            )

            errorMessage?.let {

                Text(
                    text = it,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

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
                             * Make sure student exists.
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
                             * Check active queue once more
                             * immediately before joining.
                             */
                            val queue =
                                NetworkModule.api
                                    .getAuthorityQueue(
                                        authorityId
                                    )

                            val existing =
                                queue.queue.firstOrNull {

                                    it.student_enrollment_number ==
                                            currentStudent.enrollmentNumber
                                }

                            if (existing != null) {

                                onJoined(existing.id)

                                return@launch
                            }

                            /*
                             * Join.
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

                            errorMessage =
                                "Backend error: HTTP ${e.code()}"

                        } catch (e: Exception) {

                            errorMessage =
                                e.message
                                    ?: "Unable to join the queue."

                        } finally {

                            joining = false
                        }
                    }
                },
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    !joining && student != null
            ) {

                Text(
                    if (joining)
                        "Joining..."
                    else
                        "Join Queue"
                )
            }
        }

        /*
         * Back
         */
        Button(
            onClick = onBack,
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                !joining
        ) {

            Text("Back")
        }
    }
}