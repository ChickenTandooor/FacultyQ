package com.facultyq.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.facultyq.app.ui.theme.FacultyQTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FacultyQTheme {
                FacultyQStartScreen(
                    onStudentClick = {
                        Toast.makeText(
                            this,
                            "Student login coming next",
                            Toast.LENGTH_SHORT
                        ).show()
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
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Top section
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

                    Spacer(modifier = Modifier.size(12.dp))

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

                Spacer(modifier = Modifier.height(52.dp))

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

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "CONTINUE AS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF777B7B)
                )

                Spacer(modifier = Modifier.height(12.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

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

            // Bottom section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onAdminClick() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF656969)
                    )

                    Spacer(modifier = Modifier.size(7.dp))

                    Text(
                        text = "Admin access",
                        fontSize = 12.sp,
                        color = Color(0xFF656969)
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF656969),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
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

        Spacer(modifier = Modifier.size(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF202426)
            )

            Spacer(modifier = Modifier.height(3.dp))

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