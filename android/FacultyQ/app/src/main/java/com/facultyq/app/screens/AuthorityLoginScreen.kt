package com.facultyq.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facultyq.app.data.Authority
import com.facultyq.app.data.FacultyQRepository

@Composable
fun AuthorityLoginScreen(
    onAuthoritySelected: (String) -> Unit,
    onBackClick: () -> Unit
) {

    val authorities =
        FacultyQRepository.authorities

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Authority Access",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Select your Dean or HOD account",
            style =
                MaterialTheme.typography.bodyMedium
        )

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(authorities) { authority ->

                AuthorityLoginCard(
                    authority = authority,
                    onSelected = {
                        onAuthoritySelected(
                            authority.id
                        )
                    }
                )
            }
        }

        TextButton(
            onClick = onBackClick
        ) {
            Text("Back")
        }
    }
}


@Composable
private fun AuthorityLoginCard(
    authority: Authority,
    onSelected: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = authority.name,
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    "${authority.role.name} · ${authority.department}",
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text =
                    "Cabin ${authority.cabin}",
                style =
                    MaterialTheme.typography.bodySmall
            )

            Button(
                onClick = onSelected,
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}