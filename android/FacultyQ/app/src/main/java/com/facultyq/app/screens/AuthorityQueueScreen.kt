package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import kotlinx.coroutines.delay
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
     * Load authority and the student's current queue entry.
     */
    suspend fun loadQueue() {

        authority =
            FacultyQRepository.fetchAuthorityFromBackend(
                authorityId
            )

        val response =
            NetworkModule.api.getAuthorityQueue(
                authorityId
            )

        /*
         * First try the exact queue ID.
         */
        var currentEntry =
            response.queue.firstOrNull {
                it.id == queueId &&
                        it.student_enrollment_number ==
                        enrollmentNumber
            }

        /*
         * If the exact ID isn't returned anymore,
         * look for the student's active queue entry.
         */
        if (currentEntry == null) {

            currentEntry =
                response.queue.firstOrNull {
                    it.student_enrollment_number ==
                            enrollmentNumber
                }
        }

        queueEntry = currentEntry
    }

    /*
     * Initial load.
     */
    LaunchedEffect(
        authorityId,
        enrollmentNumber,
        queueId
    ) {

        loading = true
        errorMessage = null

        try {

            loadQueue()

        } catch (e: HttpException) {

            errorMessage =
                "Backend error: HTTP ${e.code()}"

        } catch (e: Exception) {

            errorMessage =
                e.message
                    ?: "Unable to load queue information."

        } finally {

            loading = false
        }
    }

    /*
     * Automatically refresh the queue every 5 seconds.
     *
     * This runs only while this screen is visible.
     */
    LaunchedEffect(
        authorityId,
        enrollmentNumber
    ) {

        while (true) {

            delay(5000)

            try {

                val response =
                    NetworkModule.api.getAuthorityQueue(
                        authorityId
                    )

                queueEntry =
                    response.queue.firstOrNull {
                        it.student_enrollment_number ==
                                enrollmentNumber
                    }

            } catch (_: Exception) {

                /*
                 * Ignore temporary refresh failures.
                 *
                 * The existing screen remains visible.
                 */
            }
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
                text = "Loading queue...",
                modifier =
                    Modifier.padding(top = 16.dp)
            )
        }

        return
    }

    val currentAuthority = authority

    /*
     * Authority unavailable.
     */
    if (currentAuthority == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Queue information unavailable",
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
                onClick = onBackClick
            ) {

                Text("Back")
            }
        }

        return
    }

    /*
     * Queue entry disappeared from the active queue.
     *
     * This normally means the authority completed or
     * otherwise removed the active queue entry.
     */
    if (queueEntry == null) {

        Column(
            modifier = Modifier
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
                text = currentAuthority.name,
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text =
                    "${currentAuthority.role.name} • " +
                            currentAuthority.department
            )

            Text(
                text =
                    "Cabin: ${currentAuthority.cabin}"
            )

            Text(
                text =
                    "This queue entry is no longer active."
            )

            Text(
                text =
                    "The visit may have been completed or the " +
                            "queue entry may have been removed."
            )

            Button(
                onClick = onBackClick,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text("Back")
            }
        }

        return
    }

    /*
     * Main queue screen.
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Your Queue",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Text(
            text = currentAuthority.name,
            style =
                MaterialTheme.typography.titleLarge
        )

        Text(
            text =
                "${currentAuthority.role.name} • " +
                        currentAuthority.department
        )

        Text(
            text =
                "Cabin: ${currentAuthority.cabin}"
        )

        Text(
            text = "Your position"
        )

        Text(
            text =
                queueEntry!!.position.toString(),
            style =
                MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Purpose"
        )

        Text(
            text =
                queueEntry!!.purpose
        )

        Text(
            text = "Enrollment"
        )

        Text(
            text =
                queueEntry!!.student_enrollment_number
        )

        Text(
            text = "Queue status"
        )

        Text(
            text =
                queueEntry!!.status,
            style =
                MaterialTheme.typography.titleLarge
        )

        /*
         * Inform the student about the current state.
         */
        when (queueEntry!!.status) {

            "WAITING" -> {

                Text(
                    text =
                        "Please wait for your turn."
                )
            }

            "SERVING" -> {

                Text(
                    text =
                        "You are currently being served."
                )
            }

            else -> {

                Text(
                    text =
                        "Queue status: ${queueEntry!!.status}"
                )
            }
        }

        /*
         * Leave queue.
         *
         * Only allow leaving while the entry is still active.
         */
        if (
            queueEntry!!.status == "WAITING"
        ) {

            Button(
                onClick = {

                    scope.launch {

                        leavingQueue = true
                        errorMessage = null

                        try {

                            NetworkModule.api.leaveQueue(
                                queueEntry!!.id
                            )

                            onBackClick()

                        } catch (e: HttpException) {

                            errorMessage =
                                "Backend error: HTTP ${e.code()}"

                        } catch (e: Exception) {

                            errorMessage =
                                e.message
                                    ?: "Unable to leave the queue."

                        } finally {

                            leavingQueue = false
                        }
                    }
                },
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    !leavingQueue
            ) {

                Text(
                    if (leavingQueue)
                        "Leaving..."
                    else
                        "Leave Queue"
                )
            }
        }

        /*
         * Back does not leave the queue.
         */
        Button(
            onClick = onBackClick,
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                !leavingQueue
        ) {

            Text("Back")
        }
    }
}