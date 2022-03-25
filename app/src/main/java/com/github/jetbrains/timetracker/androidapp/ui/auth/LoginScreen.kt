package com.github.jetbrains.timetracker.androidapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.ui.theme.TimeTrackerAppTheme
import com.github.jetbrains.timetracker.androidapp.ui.util.TextFieldWithValidation
import com.github.jetbrains.timetracker.androidapp.util.FormatChecker

@Composable
fun LoginScreen(
    onSignIn: (email: String, password: String, name: String) -> Unit,
    onLogIn: (email: String, password: String) -> Unit,
    existingUser: Boolean = true,
    previousErrorText: String? = null,
) {
    var isExistingUser by rememberSaveable { mutableStateOf(existingUser) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Welcome to Time Tracker!",
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
        )

        if (previousErrorText != null) {
            Text(
                text = "An error occurred: $previousErrorText Please try again!",
                color = MaterialTheme.colors.error,
            )
        }

        var name by rememberSaveable { mutableStateOf("") }
        var isNameValid by rememberSaveable { mutableStateOf(true) }
        if (!isExistingUser) {
            TextFieldWithValidation(
                value = name,
                onValueChange = { name = it },
                isValid = isNameValid,
                onValidChange = { isNameValid = it },
                label = { Text("Name") },
                formatChecker = FormatChecker.NameChecker,
            )
        }

        var email by rememberSaveable { mutableStateOf("") }
        var isEmailValid by rememberSaveable { mutableStateOf(true) }
        TextFieldWithValidation(
            value = email,
            onValueChange = { email = it },
            isValid = isEmailValid,
            onValidChange = { isEmailValid = it },
            label = { Text("Email") },
            formatChecker = FormatChecker.EmailChecker,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        var password by remember { mutableStateOf("") }
        var isPassportValid by rememberSaveable { mutableStateOf(true) }
        TextFieldWithValidation(
            value = password,
            onValueChange = { password = it },
            isValid = isPassportValid,
            onValidChange = { isPassportValid = it },
            label = { Text("Password") },
            formatChecker = FormatChecker.PasswordChecker,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(modifier = Modifier.padding(8.dp))

        fun isEnabled() = email.isNotEmpty() && isEmailValid &&
                password.isNotEmpty() && isPassportValid &&
                isNameValid
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled(),
            onClick = {
                if (isExistingUser) {
                    onLogIn(email, password)
                } else {
                    onSignIn(email, password, name)
                }
            },
            content = { Text(if (isExistingUser) "Log in" else "Sign up") }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        if (isExistingUser) {
            ActionText(
                text = "Create New Account",
                onClick = {
                    isExistingUser = false
                }
            )
        } else {
            ActionText(
                text = "Log In To Existing Account",
                onClick = {
                    isExistingUser = true
                }
            )
        }
    }
}

@Composable
private fun ActionText(text: String, onClick: (Int) -> Unit) {
    ClickableText(
        text = AnnotatedString(
            text,
            spanStyle = SpanStyle(
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            )
        ),
        onClick = onClick
    )
}

@Preview
@Composable
fun SignInScreenPreview() {
    TimeTrackerAppTheme {
        LoginScreen(
            onSignIn = { _, _, _ -> },
            onLogIn = { _, _ ->  },
            previousErrorText = "error",
            existingUser = false,
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    TimeTrackerAppTheme {
        LoginScreen(
            onSignIn = { _, _, _ -> },
            onLogIn = { _, _ ->  },
            previousErrorText = "error",
            existingUser = true,
        )
    }
}