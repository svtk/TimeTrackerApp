package com.github.jetbrains.timetracker.androidapp.util

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun TextFieldDefaults.changeBorderColor() = outlinedTextFieldColors(
    cursorColor = MaterialTheme.colors.secondary,
    focusedBorderColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high),
    focusedLabelColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high),
)

@Composable
fun ClickableReadOnlyTextField(
    modifier: Modifier,
    value: String,
    label: @Composable (() -> Unit)?,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isClicked: Boolean by interactionSource.collectIsPressedAsState()
    if (isClicked) {
        onClick()
    }
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        interactionSource = interactionSource,
        readOnly = true,
        label = label
    )
}
