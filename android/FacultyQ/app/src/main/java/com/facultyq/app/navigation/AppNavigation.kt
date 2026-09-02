package com.facultyq.app.navigation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.screens.AdminScreen
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

    var currentScreen by
    remember {
        mutableStateOf("start")
    }

    var enrollmentNumber by
    remember {
        mutableStateOf(
            FacultyQRepository
                .savedStudent
                .value
                ?.enrollmentNumber
                ?: ""
        )
    }

    var selectedFacultyId by
    remember {
        mutableStateOf("")
    }

    var queueId by
    remember {
        mutableStateOf("")
    }

    val context =
        LocalContext.current

    when (currentScreen) {

        // -------------------------
        // START
        // -------------------------

        "start" -> {

            StartScreen(

                onStudentClick = {

                    val savedStudent =
                        FacultyQRepository
                            .savedStudent
                            .value

                    if (savedStudent != null) {

                        enrollmentNumber =
                            savedStudent.enrollmentNumber

                        currentScreen =
                            "facultySearch"

                    } else {

                        currentScreen =
                            "student"
                    }
                },

                onFacultyClick = {

                    currentScreen =
                        "facultyLogin"
                },

                onAdminClick = {

                    currentScreen =
                        "admin"
                },

                onFacultyRegisterClick = {

                    currentScreen =
                        "facultyRegistration"
                }
            )
        }


        // -------------------------
        // FIRST-TIME STUDENT SETUP
        // -------------------------

        "student" -> {

            StudentScreen(

                onBackClick = {

                    currentScreen =
                        "start"
                },

                onContinueClick = {

                        number ->

                    enrollmentNumber =
                        number

                    currentScreen =
                        "facultySearch"
                }
            )
        }


        // -------------------------
        // FACULTY SEARCH
        // -------------------------

        "facultySearch" -> {

            FacultySearchScreen(

                enrollmentNumber =
                    enrollmentNumber,

                onBackClick = {

                    currentScreen =
                        "start"
                },

                onApplyClick = {

                        facultyId ->

                    selectedFacultyId =
                        facultyId

                    currentScreen =
                        "queueDetails"
                }
            )
        }


        // -------------------------
        // QUEUE DETAILS
        // -------------------------

        "queueDetails" -> {

            QueueDetailsScreen(

                enrollmentNumber =
                    enrollmentNumber,

                facultyId =
                    selectedFacultyId,

                onBackClick = {

                    currentScreen =
                        "facultySearch"
                },

                onJoined = {

                        newQueueId ->

                    queueId =
                        newQueueId

                    currentScreen =
                        "queue"
                }
            )
        }


        // -------------------------
        // QUEUE
        // -------------------------

        "queue" -> {

            QueueScreen(

                enrollmentNumber =
                    enrollmentNumber,

                facultyId =
                    selectedFacultyId,

                queueId =
                    queueId,

                onBackClick = {

                    currentScreen =
                        "facultySearch"
                }
            )
        }


        // -------------------------
        // FACULTY LOGIN
        // -------------------------

        "facultyLogin" -> {

            FacultyLoginScreen(

                onBackClick = {

                    currentScreen =
                        "start"
                },

                onFacultySelected = {

                        facultyId ->

                    selectedFacultyId =
                        facultyId

                    currentScreen =
                        "facultyDashboard"
                }
            )
        }


        // -------------------------
        // FACULTY DASHBOARD
        // -------------------------

        "facultyDashboard" -> {

            FacultyDashboardScreen(

                facultyId =
                    selectedFacultyId,

                onBackClick = {

                    currentScreen =
                        "facultyLogin"
                }
            )
        }


        // -------------------------
        // FACULTY REGISTRATION
        // -------------------------

        "facultyRegistration" -> {

            FacultyRegistrationScreen(

                onBackClick = {

                    currentScreen =
                        "start"
                },

                onSubmitted = {

                    Toast.makeText(
                        context,
                        "Registration submitted for admin verification",
                        Toast.LENGTH_SHORT
                    ).show()

                    currentScreen =
                        "start"
                }
            )
        }


        // -------------------------
        // ADMIN
        // -------------------------

        "admin" -> {

            AdminScreen(

                onBackClick = {

                    currentScreen =
                        "start"
                }
            )
        }
    }
}