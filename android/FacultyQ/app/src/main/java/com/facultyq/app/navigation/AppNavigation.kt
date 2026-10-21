
package com.facultyq.app.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.screens.AdminScreen
import com.facultyq.app.screens.AuthorityDashboardScreen
import com.facultyq.app.screens.AuthorityHistoryScreen
import com.facultyq.app.screens.AuthorityLoginScreen
import com.facultyq.app.screens.AuthorityQueueDetailsScreen
import com.facultyq.app.screens.AuthorityQueueScreen
import com.facultyq.app.screens.AuthoritySearchScreen
import com.facultyq.app.screens.FacultyDashboardScreen
import com.facultyq.app.screens.FacultyLoginScreen
import com.facultyq.app.screens.FacultyRegistrationScreen
import com.facultyq.app.screens.FacultySearchScreen
import com.facultyq.app.screens.QueueDetailsScreen
import com.facultyq.app.screens.QueueScreen
import com.facultyq.app.screens.StartScreen
import com.facultyq.app.screens.StudentScreen

@Composable
fun AppNavigation() {

    var currentScreen by remember {
        mutableStateOf("start")
    }

    var enrollmentNumber by remember {
        mutableStateOf(
            FacultyQRepository.savedStudent.value?.enrollmentNumber ?: ""
        )
    }

    var selectedFacultyId by remember {
        mutableStateOf("")
    }

    var selectedAuthorityId by remember {
        mutableStateOf("")
    }

    var queueId by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    BackHandler(enabled = currentScreen != "start") {
        currentScreen = when (currentScreen) {

            // Student flow
            "student" -> "start"
            "authoritySearch" -> "start"
            "authorityQueueDetails" -> "authoritySearch"
            "authorityQueue" -> "authorityQueueDetails"

            // Older faculty flow
            "facultySearch" -> "start"
            "queueDetails" -> "facultySearch"
            "queue" -> "queueDetails"

            // Authority flow
            "authorityLogin" -> "start"
            "authorityDashboard" -> "authorityLogin"
            "authorityHistory" -> "authorityDashboard"

            // Faculty management flow
            "facultyLogin" -> "start"
            "facultyDashboard" -> "facultyLogin"
            "facultyRegistration" -> "start"

            // Admin
            "admin" -> "start"

            else -> "start"
        }
    }

    when (currentScreen) {

        // START SCREEN
        "start" -> {
            StartScreen(
                onStudentClick = {
                    val savedStudent =
                        FacultyQRepository.savedStudent.value

                    if (savedStudent != null) {
                        enrollmentNumber = savedStudent.enrollmentNumber
                        currentScreen = "authoritySearch"
                    } else {
                        currentScreen = "student"
                    }
                },
                onFacultyClick = {
                    currentScreen = "authorityLogin"
                },
                onAdminClick = {
                    currentScreen = "admin"
                },
                onFacultyRegisterClick = {
                    currentScreen = "facultyRegistration"
                }
            )
        }

        // STUDENT SETUP
        "student" -> {
            StudentScreen(
                onBackClick = {
                    currentScreen = "start"
                },
                onContinueClick = { number ->
                    enrollmentNumber = number
                    currentScreen = "authoritySearch"
                }
            )
        }

        // AUTHORITY SEARCH
        "authoritySearch" -> {
            AuthoritySearchScreen(
                onAuthoritySelected = { authorityId ->
                    selectedAuthorityId = authorityId
                    currentScreen = "authorityQueueDetails"
                },
                onBack = {
                    currentScreen = "start"
                }
            )
        }

        // AUTHORITY QUEUE DETAILS
        "authorityQueueDetails" -> {
            AuthorityQueueDetailsScreen(
                enrollmentNumber = enrollmentNumber,
                authorityId = selectedAuthorityId,
                onBack = {
                    currentScreen = "authoritySearch"
                },
                onJoined = { newQueueId ->
                    queueId = newQueueId

                    Toast.makeText(
                        context,
                        "Successfully joined the queue",
                        Toast.LENGTH_SHORT
                    ).show()

                    currentScreen = "authorityQueue"
                }
            )
        }

        // STUDENT AUTHORITY QUEUE
        "authorityQueue" -> {
            AuthorityQueueScreen(
                enrollmentNumber = enrollmentNumber,
                authorityId = selectedAuthorityId,
                queueId = queueId,
                onBackClick = {
                    currentScreen = "authoritySearch"
                }
            )
        }

        // OLDER FACULTY SEARCH
        "facultySearch" -> {
            FacultySearchScreen(
                enrollmentNumber = enrollmentNumber,
                onBackClick = {
                    currentScreen = "start"
                },
                onApplyClick = { facultyId ->
                    selectedFacultyId = facultyId
                    currentScreen = "queueDetails"
                }
            )
        }

        // OLDER FACULTY QUEUE DETAILS
        "queueDetails" -> {
            QueueDetailsScreen(
                enrollmentNumber = enrollmentNumber,
                facultyId = selectedFacultyId,
                onBackClick = {
                    currentScreen = "facultySearch"
                },
                onJoined = { newQueueId ->
                    queueId = newQueueId
                    currentScreen = "queue"
                }
            )
        }

        // OLDER FACULTY QUEUE
        "queue" -> {
            QueueScreen(
                enrollmentNumber = enrollmentNumber,
                facultyId = selectedFacultyId,
                queueId = queueId,
                onBackClick = {
                    currentScreen = "facultySearch"
                }
            )
        }

        // AUTHORITY LOGIN
        "authorityLogin" -> {
            AuthorityLoginScreen(
                onAuthoritySelected = { authorityId ->
                    selectedAuthorityId = authorityId
                    currentScreen = "authorityDashboard"
                },
                onBackClick = {
                    currentScreen = "start"
                }
            )
        }

        // AUTHORITY DASHBOARD
        "authorityDashboard" -> {
            AuthorityDashboardScreen(
                authorityId = selectedAuthorityId,
                onBackClick = {
                    currentScreen = "authorityLogin"
                },
                onHistoryClick = {
                    currentScreen = "authorityHistory"
                }
            )
        }

        // AUTHORITY APPOINTMENT HISTORY
        "authorityHistory" -> {
            AuthorityHistoryScreen(
                authorityId = selectedAuthorityId,
                onBackClick = {
                    currentScreen = "authorityDashboard"
                }
            )
        }

        // OLDER FACULTY LOGIN
        "facultyLogin" -> {
            FacultyLoginScreen(
                onBackClick = {
                    currentScreen = "start"
                },
                onFacultySelected = { facultyId ->
                    selectedFacultyId = facultyId
                    currentScreen = "facultyDashboard"
                }
            )
        }

        // OLDER FACULTY DASHBOARD
        "facultyDashboard" -> {
            FacultyDashboardScreen(
                facultyId = selectedFacultyId,
                onBackClick = {
                    currentScreen = "facultyLogin"
                }
            )
        }

        // FACULTY REGISTRATION
        "facultyRegistration" -> {
            FacultyRegistrationScreen(
                onBackClick = {
                    currentScreen = "start"
                },
                onSubmitted = {
                    Toast.makeText(
                        context,
                        "Registration submitted for admin verification",
                        Toast.LENGTH_SHORT
                    ).show()

                    currentScreen = "start"
                }
            )
        }

        // ADMIN
        "admin" -> {
            AdminScreen(
                onBackClick = {
                    currentScreen = "start"
                }
            )
        }
    }
}
