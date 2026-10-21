
package com.facultyq.app.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.facultyq.app.R
import com.facultyq.app.components.RoleCard
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    onStudentClick: () -> Unit,
    onFacultyClick: () -> Unit,
    onAdminClick: () -> Unit,
    onFacultyRegisterClick: () -> Unit
) {
    var backgroundShift by remember { mutableFloatStateOf(0f) }

    val animatedShift by animateFloatAsState(
        targetValue = backgroundShift,
        animationSpec = tween(
            durationMillis = 650,
            easing = FastOutSlowInEasing
        ),
        label = "campusBackgroundShift"
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1F3A))
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.campus_background
                ),
                contentDescription = "College campus background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight + 70.dp)
                    .offset(y = animatedShift.dp - 35.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x660B1F3A),
                                Color(0x990B1F3A),
                                Color(0xF20B1F3A)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "FQ",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "FacultyQ",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "VIRTUAL AUTHORITY QUEUE",
                    color = Color(0xFFDCE8F8),
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Skip the crowd.",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Meet your Dean or HOD without waiting in a physical line.",
                    color = Color(0xFFE6EDF7),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "CONTINUE AS",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xF7FFFFFF))
                        .clickable {
                            backgroundShift = -18f
                            onStudentClick()
                        }
                        .padding(4.dp)
                ) {
                    RoleCard(
                        icon = Icons.Default.Person,
                        title = "Student",
                        description = "Search Dean or HOD and join a queue",
                        onClick = {
                            backgroundShift = -18f
                            onStudentClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xF7FFFFFF))
                        .clickable {
                            backgroundShift = 18f
                            onFacultyClick()
                        }
                        .padding(4.dp)
                ) {
                    RoleCard(
                        icon = Icons.Default.Person,
                        title = "Authority",
                        description = "Manage your availability and queue",
                        onClick = {
                            backgroundShift = 18f
                            onFacultyClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        backgroundShift = 10f
                        onFacultyRegisterClick()
                    }
                ) {
                    Text(
                        text = "Authority registration",
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = {
                        backgroundShift = -10f
                        onAdminClick()
                    }
                ) {
                    Text(
                        text = "Admin access",
                        color = Color.White
                    )
                }
            }
        }
    }
}
