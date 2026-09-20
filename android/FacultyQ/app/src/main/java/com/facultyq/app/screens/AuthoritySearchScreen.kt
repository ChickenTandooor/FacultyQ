package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.Authority
import com.facultyq.app.data.FacultyQRepository

@Composable
fun AuthoritySearchScreen(
    onAuthoritySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    var authorities by remember {
        mutableStateOf<List<Authority>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {

        try {

            authorities =
                FacultyQRepository.fetchAuthoritiesFromBackend()

            errorMessage = null

        } catch (e: Exception) {

            errorMessage =
                "${e.javaClass.simpleName}: ${e.message}"

        } finally {

            loading = false
        }
    }

    val filteredAuthorities =
        authorities.filter { authority ->

            authority.name.contains(
                searchQuery,
                ignoreCase = true
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Find Authority",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            "Search for a Dean or HOD",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search by name")
            },
            singleLine = true
        )

        when {

            loading -> {

                Text(
                    "Loading authorities..."
                )
            }

            errorMessage != null -> {

                Text(
                    "Unable to load authorities.\n\n$errorMessage"
                )
            }

            filteredAuthorities.isEmpty() -> {

                Text(
                    "No authorities found."
                )
            }

            else -> {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(filteredAuthorities) { authority ->

                        AuthorityCard(
                            authority = authority,
                            onClick = {
                                onAuthoritySelected(
                                    authority.id
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthorityCard(
    authority: Authority,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            Text(
                authority.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                if (authority.role == com.facultyq.app.data.AuthorityRole.HOD) {
                    "${authority.department} HOD"
                } else {
                    "DEAN"
                },
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                authority.department,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Cabin ${authority.cabin}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                authority.status.name
                    .replace("_", " "),
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                "Queue capacity: ${authority.queueCapacity}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}