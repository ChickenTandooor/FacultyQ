
package com.facultyq.app.screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.facultyq.app.network.NetworkModule
import com.facultyq.app.network.QueueEntryDto
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.text.format.DateFormat

private val HistoryNavy = Color(0xFF0B1F3A)
private val HistoryCard = Color(0xF7FFFFFF)
private val HistoryMuted = Color(0xFF526176)

@Composable
fun AuthorityHistoryScreen(
    authorityId: String,
    onBackClick: () -> Unit
) {
    var history by remember { mutableStateOf<List<QueueEntryDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    suspend fun loadHistory() {
        val response = NetworkModule.api.getAuthorityHistory(authorityId)
        history = response.history
    }

    LaunchedEffect(authorityId) {
        loading = true
        errorMessage = null
        try {
            loadHistory()
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unable to load appointment history."
        } finally {
            loading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1D416B),
                        HistoryNavy
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "FacultyQ",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFDCE9FF)
            )

            Text(
                text = "Appointment History",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Text(
                text = "Completed appointments for this authority",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE6EDF7)
            )

            if (!loading && errorMessage == null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = HistoryCard
                ) {
                    Text(
                        text = "${history.size} completed appointment(s)",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        color = HistoryNavy,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = "Loading history...",
                            color = Color.White
                        )
                    }
                }
            } else {
                errorMessage?.let { message ->
                    Surface(
                        color = Color(0xFFFFEEEE),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(14.dp),
                            color = Color(0xFFB42318)
                        )
                    }
                }

                if (errorMessage == null && history.isEmpty()) {
                    Surface(
                        color = HistoryCard,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "No completed appointments yet. Completed visits will appear here.",
                            modifier = Modifier.padding(16.dp),
                            color = HistoryMuted
                        )
                    }
                } else if (errorMessage == null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = history,
                            key = { it.id }
                        ) { entry ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = HistoryCard,
                                shadowElevation = 3.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(15.dp),
                                    verticalArrangement = Arrangement.spacedBy(7.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = entry.student_enrollment_number,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = HistoryNavy
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            text = "COMPLETED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF16845B)
                                        )
                                    }

                                    Text(
                                        text = "Class: ${entry.student_current_class}",
                                        color = HistoryMuted
                                    )

                                    Text(
                                        text = "Purpose: ${entry.purpose}",
                                        color = HistoryMuted
                                    )

                                    Text(
                                        text = "Completed: ${formatCompletionTime(entry.completed_at)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HistoryNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            refreshing = true
                            errorMessage = null
                            try {
                                loadHistory()
                            } catch (e: Exception) {
                                errorMessage =
                                    e.message ?: "Unable to refresh history."
                            } finally {
                                refreshing = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !loading && !refreshing
                ) {
                    Text(if (refreshing) "Refreshing..." else "Refresh")
                }

                Button(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
            }
        }
    }
}



private fun formatCompletionTime(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Time unavailable (older record)"
    }

    return try {
        val normalized = value.trim()

        // Parse ISO timestamps with an explicit timezone offset.
        val isoFormat = java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            java.util.Locale.US
        ).apply {
            isLenient = false
        }

        val date = try {
            isoFormat.parse(normalized)
        } catch (_: Exception) {
            null
        } ?: java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            java.util.Locale.US
        ).parse(normalized)
        ?: return normalized

        java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(date)
    } catch (_: Exception) {
        value
    }
}


