package com.example.subscriptions_app.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val appTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = GraphikTrial,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    displayMedium = TextStyle(
        fontFamily = GraphikTrial,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    displaySmall = TextStyle(
        fontFamily = GraphikTrial,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GraphikTrial,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GraphikTrial,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = DarkGray
    ),
    labelLarge = TextStyle(
        fontFamily = GraphikTrial,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    )
)
