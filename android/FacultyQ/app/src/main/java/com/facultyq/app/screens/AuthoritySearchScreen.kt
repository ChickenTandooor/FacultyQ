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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.Authority
import com.facultyq.app.data.FacultyQRepository

@Composable
fun AuthoritySearchScreen(
    onAuthoritySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    val authorities = FacultyQRepository.searchAuthorities(searchQuery)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Find Authority",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Search for a Dean or HOD",
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(authorities) { authority ->

                AuthorityCard(
                    authority = authority,
                    onClick = {
                        onAuthoritySelected(authority.id)
                    }
                )
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = authority.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = authority.role.name,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = authority.department,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Cabin ${authority.cabin}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = authority.status.name.replace("_", " "),
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Queue capacity: ${authority.queueCapacity}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}