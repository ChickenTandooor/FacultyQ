
package com.facultyq.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.facultyq.app.R
import com.facultyq.app.data.Authority
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.network.AuthorityStatusUpdateRequest
import com.facultyq.app.network.NetworkModule
import com.facultyq.app.network.QueueEntryDto
import kotlinx.coroutines.launch

private val DashboardNavy = Color(0xFF0B1F3A)
private val DashboardBlue = Color(0xFF1D416B)
private val DashboardWhite = Color(0xF7FFFFFF)
private val DashboardMuted = Color(0xFF526176)

private val authorityStatuses = listOf(
    "AVAILABLE",
    "BUSY",
    "DO_NOT_DISTURB",
    "AWAY"
)

@Composable
fun AuthorityDashboardScreen(
    authorityId: String,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var authority by remember { mutableStateOf<Authority?>(null) }
    var queue by remember { mutableStateOf<List<QueueEntryDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var actionQueueId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf("AVAILABLE") }
    var savingStatus by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadDashboard() {
        authority = FacultyQRepository.fetchAuthorityFromBackend(authorityId)
        val response = NetworkModule.api.getAuthorityQueue(authorityId)
        queue = response.queue
        selectedStatus = authority?.status?.name ?: "AVAILABLE"
    }

    LaunchedEffect(authorityId) {
        loading = true
        errorMessage = null
        try {
            loadDashboard()
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unable to load dashboard."
        } finally {
            loading = false
        }
    }

    val currentAuthority = authority

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardNavy)
    ) {
        Image(
            painter = painterResource(id = R.drawable.campus_background),
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
                            Color(0x990B1F3A),
                            Color(0xCC0B1F3A),
                            Color(0xF20B1F3A)
                        )
                    )
                )
        )

        when {
            loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text(
                        text = "Loading dashboard...",
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            currentAuthority == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DashboardWhite
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Authority not found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = DashboardNavy
                            )
                            errorMessage?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Button(
                                onClick = onBackClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Back")
                            }
                        }
                    }
                }
            }

            else -> {
                val waitingStudents = queue.filter { it.status == "WAITING" }
                val servingStudents = queue.filter { it.status == "SERVING" }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "FacultyQ",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFDCE9FF),
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Authority Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    // Authority information and status controls
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = DashboardWhite,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = currentAuthority.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = DashboardNavy,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${currentAuthority.role.name} • ${currentAuthority.department}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DashboardMuted
                            )

                            Text(
                                text = "Cabin: ${currentAuthority.cabin}",
                                color = DashboardMuted
                            )

                            Text(
                                text = "Current status: ${currentAuthority.status.name}",
                                color = DashboardBlue,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Change your status",
                                style = MaterialTheme.typography.titleSmall,
                                color = DashboardNavy,
                                fontWeight = FontWeight.Bold
                            )

                            authorityStatuses.chunked(2).forEach { statusRow ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    statusRow.forEach { status ->
                                        val isSelected = selectedStatus == status

                                        if (isSelected) {
                                            Button(
                                                onClick = {
                                                    selectedStatus = status
                                                    statusMessage = null
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                    horizontal = 4.dp
                                                ),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = DashboardNavy
                                                )
                                            ) {
                                                Text(
                                                    text = status.replace("_", " "),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = {
                                                    selectedStatus = status
                                                    statusMessage = null
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                    horizontal = 4.dp
                                                )
                                            ) {
                                                Text(
                                                    text = status.replace("_", " "),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }

                                    if (statusRow.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        savingStatus = true
                                        errorMessage = null
                                        statusMessage = null

                                        try {
                                            NetworkModule.api.updateAuthorityStatus(
                                                authorityId = authorityId,
                                                request = AuthorityStatusUpdateRequest(
                                                    status = selectedStatus
                                                )
                                            )
                                            loadDashboard()
                                            statusMessage = "Status updated successfully."
                                        } catch (e: Exception) {
                                            errorMessage =
                                                e.message ?: "Unable to update status."
                                        } finally {
                                            savingStatus = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                enabled = !savingStatus && !actionQueueId.let { it != null },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DashboardBlue
                                )
                            ) {
                                Text(
                                    if (savingStatus) "Saving status..." else "Save status"
                                )
                            }

                            statusMessage?.let {
                                Text(
                                    text = it,
                                    color = Color(0xFF16845B),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Queue summary
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = DashboardWhite
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Queue Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = DashboardNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${waitingStudents.size} / ${currentAuthority.queueCapacity} students waiting",
                                color = DashboardMuted
                            )
                            if (servingStudents.isNotEmpty()) {
                                Text(
                                    text = "${servingStudents.size} student(s) currently being served",
                                    color = DashboardBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    errorMessage?.let {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEEEE)
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFFB42318)
                            )
                        }
                    }

                    Text(
                        text = "Student Queue",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    if (queue.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = DashboardWhite
                        ) {
                            Text(
                                text = "No students are currently waiting.",
                                modifier = Modifier.padding(16.dp),
                                color = DashboardMuted
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = queue,
                                key = { it.id }
                            ) { entry ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    color = DashboardWhite,
                                    shadowElevation = 3.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(7.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "#${entry.position}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = DashboardNavy
                                            )
                                            Spacer(
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                            Text(
                                                text = entry.student_enrollment_number,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = DashboardNavy,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Text(
                                            text = "Purpose: ${entry.purpose}",
                                            color = DashboardMuted
                                        )
                                        Text(
                                            text = "Class: ${entry.student_current_class}",
                                            color = DashboardMuted
                                        )
                                        Text(
                                            text = "Status: ${entry.status}",
                                            color = if (entry.status == "SERVING") {
                                                Color(0xFF16845B)
                                            } else {
                                                DashboardBlue
                                            },
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        if (entry.status == "WAITING") {
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        actionQueueId = entry.id
                                                        errorMessage = null
                                                        try {
                                                            NetworkModule.api.serveQueue(entry.id)
                                                            loadDashboard()
                                                        } catch (e: Exception) {
                                                            errorMessage =
                                                                e.message ?: "Unable to serve student."
                                                        } finally {
                                                            actionQueueId = null
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(46.dp),
                                                enabled = actionQueueId == null,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = DashboardNavy
                                                )
                                            ) {
                                                Text(
                                                    if (actionQueueId == entry.id) {
                                                        "Serving..."
                                                    } else {
                                                        "Serve"
                                                    }
                                                )
                                            }
                                        }

                                        if (entry.status == "SERVING") {
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        actionQueueId = entry.id
                                                        errorMessage = null
                                                        try {
                                                            NetworkModule.api.completeQueue(entry.id)
                                                            loadDashboard()
                                                        } catch (e: Exception) {
                                                            errorMessage =
                                                                e.message ?: "Unable to complete queue entry."
                                                        } finally {
                                                            actionQueueId = null
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(46.dp),
                                                enabled = actionQueueId == null,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF16845B)
                                                )
                                            ) {
                                                Text(
                                                    if (actionQueueId == entry.id) {
                                                        "Completing..."
                                                    } else {
                                                        "Complete"
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onHistoryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = actionQueueId == null && !savingStatus,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DashboardBlue
                        )
                    ) {
                        Text("Appointment History")
                    }
                }
            }
        }
    }
}
