package com.itsallprivate.bluetoothchat.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun WithConfirmation(
    title: String = "Please Confirm",
    message: String = "Are you sure you want to perform this action?",
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }

    content { showConfirmationDialog = true }

    // Show confirmation dialog if needed
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        onConfirm()
                    },
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.error,
                        // this button will never be disabled
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent,
                    ),
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmationDialog = false },
                ) {
                    Text(cancelText)
                }
            },
        )
    }
}
