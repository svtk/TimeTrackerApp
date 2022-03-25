package com.github.jetbrains.timetracker.androidapp.util

import android.util.Patterns

interface FormatChecker {
    fun isValid(text: String): Boolean
    fun getErrorMessage(text: String): String

    object EmailChecker : FormatChecker {
        override fun isValid(text: String) =
            Patterns.EMAIL_ADDRESS.matcher(text).matches()

        override fun getErrorMessage(text: String) = "Email is expected"
    }

    object PasswordChecker : FormatChecker {
        override fun isValid(text: String) = text.length >= 6

        override fun getErrorMessage(text: String) = "Password should be at least 6 characters"
    }

    object NameChecker : FormatChecker {
        override fun isValid(text: String) = text.isNotBlank()

        override fun getErrorMessage(text: String) = "Name should be not empty"
    }
}