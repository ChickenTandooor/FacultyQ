package com.facultyq.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.facultyq.app.ui.theme.FacultyQTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FacultyQTheme {

                var currentScreen by remember {
                    mutableStateOf("start")
                }

                var enrollmentNumber by remember {
                    mutableStateOf("")
                }

                when (currentScreen) {

                    "start" -> {

                        FacultyQStartScreen(

                            onStudentClick = {
                                currentScreen = "student"
                            },

                            onFacultyClick = {
                                Toast.makeText(
                                    this,
                                    "Faculty login coming next",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onAdminClick = {
                                Toast.makeText(
                                    this,
                                    "Admin login coming next",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    "student" -> {

                        StudentScreen(

                            onBackClick = {
                                currentScreen = "start"
                            },

                            onContinueClick = { number ->

                                enrollmentNumber = number
                                currentScreen = "facultySearch"
                            }
                        )
                    }

                    "facultySearch" -> {

                        FacultySearchScreen(

                            enrollmentNumber = enrollmentNumber,

                            onBackClick = {
                                currentScreen = "student"
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FacultyQStartScreen(
    onStudentClick: () -> Unit,
    onFacultyClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF202426)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FQ",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.size(12.dp)
                    )

                    Column {

                        Text(
                            text = "FacultyQ",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF202426)
                        )

                        Text(
                            text = "Virtual faculty queue",
                            fontSize = 12.sp,
                            color = Color(0xFF737777)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(52.dp)
                )

                Text(
                    text = "Skip the crowd.",
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202426)
                )

                Text(
                    text = "Meet your faculty without waiting in a physical line.",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF666A6A)
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Text(
                    text = "CONTINUE AS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF777B7B)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                RoleCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF202426),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    title = "Student",
                    description = "Search faculty and join a queue",
                    onClick = onStudentClick
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                RoleCard(
                    icon = {
                        Text(
                            text = "F",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF202426)
                        )
                    },
                    title = "Faculty",
                    description = "Manage your availability and queue",
                    onClick = onFacultyClick
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onAdminClick()
                        }
                        .padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "A",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF656969)
                    )

                    Spacer(
                        modifier = Modifier.size(7.dp)
                    )

                    Text(
                        text = "Admin access",
                        fontSize = 12.sp,
                        color = Color(0xFF656969)
                    )

                    Spacer(
                        modifier = Modifier.size(5.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF656969),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "FacultyQ • Campus Queue Management",
                    fontSize = 10.sp,
                    color = Color(0xFF999C9C)
                )
            }
        }
    }
}


@Composable
fun RoleCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF9F9F7))
            .border(
                width = 1.dp,
                color = Color(0xFFD4D6D4),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8E9E7)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF202426)
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF747878)
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Continue",
            tint = Color(0xFF555959),
            modifier = Modifier.size(19.dp)
        )
    }
}


@Composable
fun StudentScreen(
    onBackClick: () -> Unit,
    onContinueClick: (String) -> Unit
) {

    BackHandler {
        onBackClick()
    }
    val context = LocalContext.current
    var enrollmentNumber by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F5))
            .padding(
                horizontal = 22.dp,
                vertical = 20.dp
            )
    ) {

        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onBackClick()
                }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF333333)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Back",
                fontSize = 13.sp,
                color = Color(0xFF555555)
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Text(
            text = "Student",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF222222)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Enter your enrollment number to find your faculty and join a queue.",
            fontSize = 13.sp,
            color = Color(0xFF777777),
            lineHeight = 19.sp
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Text(
            text = "Enrollment number",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF444444)
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        OutlinedTextField(
            value = enrollmentNumber,
            onValueChange = {
                enrollmentNumber = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Enter your enrollment number",
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                if (enrollmentNumber.isBlank()) {

                    Toast.makeText(
                        context,
                        "Please enter your enrollment number",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    onContinueClick(enrollmentNumber)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {

            Text(
                text = "Continue",
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Continue",
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Your enrollment number is used to identify you in the queue.",
            fontSize = 11.sp,
            color = Color(0xFF888888),
            lineHeight = 16.sp
        )
    }
}


@Composable
fun FacultySearchScreen(
    enrollmentNumber: String,
    onBackClick: () -> Unit
) {

    BackHandler {
        onBackClick()
    }

    val context = LocalContext.current

    var facultySearch by remember {
        mutableStateOf("")
    }

    var showFacultyResult by remember {
        mutableStateOf(false)
    }

    val currentDateTime = remember {
        SimpleDateFormat(
            "dd MMM yyyy  •  hh:mm a",
            Locale.getDefault()
        ).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F5))
            .padding(
                horizontal = 22.dp,
                vertical = 20.dp
            )
    ) {

        // Back
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onBackClick()
                }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF333333)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Back",
                fontSize = 13.sp,
                color = Color(0xFF555555)
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // Current date and time
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEDEDE9))
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                )
        ) {

            Text(
                text = currentDateTime,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF444444)
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // Enrollment number
        Text(
            text = "Enrollment No: $enrollmentNumber",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Text(
            text = "Find your faculty",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF222222)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Search for the faculty member you want to meet.",
            fontSize = 12.sp,
            color = Color(0xFF777777),
            lineHeight = 18.sp
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // Faculty search
        OutlinedTextField(
            value = facultySearch,
            onValueChange = {
                facultySearch = it
                showFacultyResult = false
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search faculty",
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(19.dp),
                    tint = Color(0xFF666666)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {

                if (facultySearch.isBlank()) {

                    Toast.makeText(
                        context,
                        "Enter a faculty name",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    showFacultyResult = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = "Search",
                fontSize = 13.sp
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (showFacultyResult) {

            FacultyResultCard(
                facultyName = facultySearch,
                onApplyClick = {

                    Toast.makeText(
                        context,
                        "Queue request submitted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}


@Composable
fun FacultyResultCard(
    facultyName: String,
    onApplyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF9F9F7))
            .border(
                width = 1.dp,
                color = Color(0xFFD4D6D4),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            )
    ) {

        Text(
            text = "Dr. Sharma",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF202426)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Cabin: N601",
            fontSize = 13.sp,
            color = Color(0xFF555959)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF20C940))
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = "Available",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF444444)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Current queue: 0",
            fontSize = 13.sp,
            color = Color(0xFF555959)
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = onApplyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(11.dp)
        ) {

            Text(
                text = "Apply for Queue",
                fontSize = 13.sp
            )
        }
    }
}