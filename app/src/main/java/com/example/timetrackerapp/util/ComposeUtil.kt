package com.example.timetrackerapp.util

import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun TextFieldDefaults.changeBorderColor() = outlinedTextFieldColors(
    cursorColor = MaterialTheme.colors.secondary,
    focusedBorderColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high),
    focusedLabelColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high),
)