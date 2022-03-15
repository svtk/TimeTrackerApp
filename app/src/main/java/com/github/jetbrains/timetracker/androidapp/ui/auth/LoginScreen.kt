package com.github.jetbrains.timetracker.androidapp.ui.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.User
import com.github.jetbrains.timetracker.androidapp.model.toUser
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    onLogin: (User?) -> Unit,
    onFail: () -> Unit,
) {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = userEmail,
            onValueChange = { text -> userEmail = text },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
        )
        OutlinedTextField(
            value = userPassword,
            onValueChange = { text -> userPassword = text },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                            onLogin(auth.currentUser?.toUser())
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            onLogin(null)
                            onFail()
                        }
                    }

            },
            content = { Text("Sign in") }
        )
    }
}