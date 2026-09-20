package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch

@Composable
fun AuthorityDashboardScreen(
    authorityId: String,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var authority by remember {
        mutableStateOf<Authority?>(null)
    }

    var queue by remember {
        mutableStateOf<List<QueueEntryDto>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var actionQueueId by remember {
        mutableStateOf<String?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Load authority and queue.
     */
    suspend fun loadDashboard() {

        authority =
            FacultyQRepository.fetchAuthorityFromBackend(
                authorityId
            )

        val response =
            NetworkModule.api.getAuthorityQueue(
                authorityId
            )

        queue = response.queue
    }

    /*
     * Initial load.
     */
    LaunchedEffect(authorityId) {

        loading = true
        errorMessage = null

        try {
            loadDashboard()
        } catch (e: Exception) {

            errorMessage =
                e.message ?: "Unable to load dashboard."

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
            verticalArrangement = Arrangement.Center
        ) {

            CircularProgressIndicator()

            Text(
                text = "Loading dashboard...",
                modifier = Modifier.padding(top = 16.dp)
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
                onClick = onBackClick
            ) {
                Text("Back")
            }
        }

        return
    }

    /*
     * Only WAITING students count toward the waiting count.
     */
    val waitingStudents =
        queue.filter {
            it.status == "WAITING"
        }

    val servingStudents =
        queue.filter {
            it.status == "SERVING"
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Authority Dashboard",
            style = MaterialTheme.typography.headlineSmall
        )

        /*
         * Authority information.
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = currentAuthority.name,
                style = MaterialTheme.typography.titleLarge
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
                    "Status: ${currentAuthority.status.name}"
            )
        }

        /*
         * Queue summary.
         */
        Text(
            text = "Queue Status",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text =
                "${waitingStudents.size} / " +
                        "${currentAuthority.queueCapacity} " +
                        "students waiting"
        )

        if (servingStudents.isNotEmpty()) {

            Text(
                text =
                    "${servingStudents.size} student(s) currently being served"
            )
        }

        errorMessage?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        /*
         * Queue list.
         */
        if (queue.isEmpty()) {

            Text(
                text = "No students are currently waiting."
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = queue,
                    key = {
                        it.id
                    }
                ) { entry ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text =
                                "#${entry.position}  " +
                                        entry.student_enrollment_number,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text =
                                "Purpose: ${entry.purpose}"
                        )

                        Text(
                            text =
                                "Class: " +
                                        entry.student_current_class
                        )

                        Text(
                            text =
                                "Status: ${entry.status}"
                        )

                        /*
                         * WAITING → SERVING
                         */
                        if (entry.status == "WAITING") {

                            Button(
                                onClick = {

                                    scope.launch {

                                        actionQueueId = entry.id
                                        errorMessage = null

                                        try {

                                            NetworkModule.api
                                                .serveQueue(entry.id)

                                            /*
                                             * Reload from PostgreSQL
                                             * after the action.
                                             */
                                            loadDashboard()

                                        } catch (e: Exception) {

                                            errorMessage =
                                                e.message
                                                    ?: "Unable to serve student."

                                        } finally {

                                            actionQueueId = null
                                        }
                                    }
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                enabled =
                                    actionQueueId == null
                            ) {

                                Text(
                                    if (actionQueueId == entry.id)
                                        "Serving..."
                                    else
                                        "Serve"
                                )
                            }
                        }

                        /*
                         * SERVING → COMPLETED
                         */
                        if (entry.status == "SERVING") {

                            Button(
                                onClick = {

                                    scope.launch {

                                        actionQueueId = entry.id
                                        errorMessage = null

                                        try {

                                            NetworkModule.api
                                                .completeQueue(entry.id)

                                            /*
                                             * Reload from PostgreSQL.
                                             */
                                            loadDashboard()

                                        } catch (e: Exception) {

                                            errorMessage =
                                                e.message
                                                    ?: "Unable to complete queue entry."

                                        } finally {

                                            actionQueueId = null
                                        }
                                    }
                                },

                                modifier =
                                    Modifier.fillMaxWidth(),

                                enabled =
                                    actionQueueId == null
                            ) {

                                Text(
                                    if (actionQueueId == entry.id)
                                        "Completing..."
                                    else
                                        "Complete"
                                )
                            }
                        }
                    }
                }
            }
        }

        /*
         * Back
         */
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = actionQueueId == null
        ) {
            Text("Back")
        }
    }
}