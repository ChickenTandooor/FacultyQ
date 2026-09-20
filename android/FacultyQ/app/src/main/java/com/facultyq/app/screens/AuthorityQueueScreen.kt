package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.facultyq.app.network.NetworkModule
import com.facultyq.app.network.QueueEntryDto
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun AuthorityQueueScreen(
    enrollmentNumber: String,
    authorityId: String,
    queueId: String,
    onBackClick: () -> Unit
) {

    var authority by remember {
        mutableStateOf<Authority?>(null)
    }

    var queueEntry by remember {
        mutableStateOf<QueueEntryDto?>(null)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var leavingQueue by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    /*
     * Load authority and queue from Docker backend.
     */
    LaunchedEffect(
        authorityId,
        enrollmentNumber,
        queueId
    ) {

        loading = true
        errorMessage = null

        try {

            /*
             * Load authority from backend.
             */
            authority =
                FacultyQRepository
                    .fetchAuthorityFromBackend(
                        authorityId
                    )

            if (authority == null) {

                errorMessage =
                    "Unable to load authority from backend."

                loading = false
                return@LaunchedEffect
            }

            /*
             * Load the complete authority queue
             * from the backend.
             */
            val response =
                NetworkModule.api.getAuthorityQueue(
                    authorityId
                )

            /*
             * First try the exact queue ID returned
             * when the student joined.
             */
            queueEntry =
                response.queue.firstOrNull { entry ->

                    entry.id == queueId &&
                            entry.student_enrollment_number ==
                            enrollmentNumber
                }

            /*
             * If that isn't found, look for the student's
             * active WAITING entry.
             */
            if (queueEntry == null) {

                queueEntry =
                    response.queue.firstOrNull { entry ->

                        entry.student_enrollment_number ==
                                enrollmentNumber &&
                                entry.status.equals(
                                    "WAITING",
                                    ignoreCase = true
                                )
                    }
            }

            if (queueEntry == null) {

                errorMessage =
                    "No active queue entry was found " +
                            "for this student."

            }

        } catch (error: HttpException) {

            error.printStackTrace()

            errorMessage =
                "Backend error: HTTP ${error.code()}"

        } catch (error: Exception) {

            error.printStackTrace()

            errorMessage =
                "Unable to load queue: " +
                        (error.message
                            ?: "Unknown error")
        }

        loading = false
    }

    /*
     * Loading screen
     */
    if (loading) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Loading your queue...",
                style =
                    MaterialTheme.typography.headlineSmall
            )
        }

        return
    }

    /*
     * Error screen
     */
    if (
        authority == null ||
        queueEntry == null
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Queue information unavailable",
                style =
                    MaterialTheme.typography.headlineSmall
            )

            Text(
                text =
                    errorMessage
                        ?: "Unknown queue error.",

                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text =
                    "Authority: $authorityId",

                style =
                    MaterialTheme.typography.bodySmall
            )

            Text(
                text =
                    "Queue ID: $queueId",

                style =
                    MaterialTheme.typography.bodySmall
            )

            Text(
                text =
                    "Enrollment: $enrollmentNumber",

                style =
                    MaterialTheme.typography.bodySmall
            )

            Button(
                onClick = onBackClick
            ) {
                Text("Back")
            }
        }

        return
    }

    val selectedAuthority =
        authority!!

    val selectedQueueEntry =
        queueEntry!!

    /*
     * Successful queue screen
     */
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Your Queue",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Text(
            text = selectedAuthority.name,
            style =
                MaterialTheme.typography.titleLarge
        )

        Text(
            text =
                "${selectedAuthority.role.name} · " +
                        selectedAuthority.department,

            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text =
                "Cabin ${selectedAuthority.cabin}",

            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Your position",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                selectedQueueEntry.position.toString(),

            style =
                MaterialTheme.typography.displaySmall
        )

        Text(
            text = "Purpose",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                selectedQueueEntry.purpose,

            style =
                MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Enrollment",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                enrollmentNumber,

            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Queue status",
            style =
                MaterialTheme.typography.labelMedium
        )

        Text(
            text =
                selectedQueueEntry.status,

            style =
                MaterialTheme.typography.bodyMedium
        )

        /*
         * Leave Queue
         */
        Button(
            onClick = {

                scope.launch {

                    leavingQueue = true

                    try {

                        NetworkModule.api.leaveQueue(
                            selectedQueueEntry.id
                        )

                        onBackClick()

                    } catch (error: HttpException) {

                        error.printStackTrace()

                        errorMessage =
                            "Unable to leave queue: " +
                                    "HTTP ${error.code()}"

                    } catch (error: Exception) {

                        error.printStackTrace()

                        errorMessage =
                            "Unable to leave queue: " +
                                    (error.message
                                        ?: "Unknown error")

                    } finally {

                        leavingQueue = false
                    }
                }
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled = !leavingQueue
        ) {

            Text(
                if (leavingQueue) {
                    "Leaving..."
                } else {
                    "Leave Queue"
                }
            )
        }

        /*
         * Back does NOT leave the queue.
         */
        Button(
            onClick = onBackClick,

            modifier =
                Modifier.fillMaxWidth(),

            enabled = !leavingQueue
        ) {

            Text("Back")
        }
    }
}