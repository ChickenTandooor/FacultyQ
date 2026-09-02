package com.facultyq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.facultyq.app.data.FacultyQRepository
import com.facultyq.app.navigation.AppNavigation
import com.facultyq.app.ui.theme.FacultyQTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        FacultyQRepository.initialize(
            applicationContext
        )

        setContent {

            FacultyQTheme {

                AppNavigation()
            }
        }
    }
}