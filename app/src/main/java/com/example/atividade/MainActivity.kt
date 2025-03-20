package com.example.atividade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                PomodoroTimer(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroTimerPreview() {
    PomodoroTimer()
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    valueOf: String,
    changeValue: (String) -> Unit,
    labelText: String,
) {
    TextField(
        value = valueOf,
        label = {
            Text(text = labelText)
        },
        modifier = modifier,
        onValueChange = {
            changeValue(it)
        }
    )
}

@Composable
fun PomodoroTimer(modifier: Modifier = Modifier) {
    var sessionDuration by remember { mutableStateOf(25f) }
    var timeLeftInMillis by remember { mutableStateOf(25 * 60 * 1000L) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var sessionName by remember { mutableStateOf("Foco") }
    var completedSessions by remember { mutableStateOf(0) }
    var isWorkMode by remember { mutableStateOf(true) }
    val progress = 1f - (timeLeftInMillis / (sessionDuration * 60 * 1000f))

    val darkPurple = Color(0xFF261C2C)
    val mediumPurple = Color(0xFF3E2C41)
    val lightPurple = Color(0xFF5C527F)
    val brightPurple = Color(0xFF9370DB)
    val lilac = Color(0xFFB19CD9)

    val backgroundColor by animateColorAsState(
        targetValue = if (isWorkMode) darkPurple else Color(0xFF272336),
        animationSpec = tween(durationMillis = 1000), label = "backgroundColorAnimation"
    )

    val primaryColor by animateColorAsState(
        targetValue = if (isWorkMode) brightPurple else lilac,
        animationSpec = tween(durationMillis = 1000), label = "primaryColorAnimation"
    )

    DisposableEffect(Unit) {
        onDispose {
            isTimerRunning = false
        }
    }

    LaunchedEffect(isTimerRunning, sessionDuration) {
        if (isTimerRunning) {
            while (timeLeftInMillis > 0 && isTimerRunning) {
                delay(1000L)
                timeLeftInMillis -= 1000L
            }

            if (timeLeftInMillis <= 0 && isTimerRunning) {
                isTimerRunning = false

                if (isWorkMode) {
                    completedSessions++
                    isWorkMode = false
                    sessionName = "Descanso"
                    sessionDuration = 5f
                } else {
                    isWorkMode = true
                    sessionName = "Foco"
                    sessionDuration = 25f
                }

                timeLeftInMillis = (sessionDuration * 60 * 1000L).toLong()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "POMODORO TIMER",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = mediumPurple
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sessão de $sessionName",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sessões completadas: $completedSessions",
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                    label = "progressAnimation"
                )

                Canvas(modifier = Modifier.size(280.dp)) {
                    drawArc(
                        color = lightPurple.copy(alpha = 0.3f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(mediumPurple.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val minutes = (timeLeftInMillis / 1000) / 60
                        val seconds = (timeLeftInMillis / 1000) % 60

                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp,
                            color = Color.White
                        )

                        Text(
                            text = if (isWorkMode) "Mantenha o foco!" else "Descanse um pouco",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (!isTimerRunning) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = mediumPurple
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Duração: ${sessionDuration.toInt()} minutos",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = sessionDuration,
                            onValueChange = {
                                sessionDuration = it
                                timeLeftInMillis = (sessionDuration * 60 * 1000).toLong()
                            },
                            valueRange = 1f..60f,
                            steps = 59,
                            colors = SliderDefaults.colors(
                                thumbColor = primaryColor,
                                activeTrackColor = primaryColor,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { isTimerRunning = !isTimerRunning },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                ) {
                    Text(
                        text = if (isTimerRunning) "II" else "▶",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                IconButton(
                    onClick = {
                        isTimerRunning = false
                        timeLeftInMillis = (sessionDuration * 60 * 1000).toLong()
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(lightPurple.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "↻",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}