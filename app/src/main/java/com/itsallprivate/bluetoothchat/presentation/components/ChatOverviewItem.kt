package com.itsallprivate.bluetoothchat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import com.itsallprivate.bluetoothchat.domain.chat.ChatOverview
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatOverviewItem(
    chatOverview: ChatOverview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .padding(start = 16.dp, end = 32.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = getInitials(chatOverview.name ?: chatOverview.address),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = chatOverview.name ?: chatOverview.address,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                chatOverview.latestMessage?.let {
                    Text(
                        text = formatDateTime(it.dateTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            chatOverview.latestMessage?.let {
                val textPrefix = if (it.isFromLocalUser) "You: " else ""
                Text(
                    text = textPrefix + it.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

fun getInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex())
    return if (parts.size >= 2) {
        "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
    } else if (parts.isNotEmpty()) {
        parts.first().first().uppercaseChar().toString()
    } else {
        ""
    }
}

fun formatDateTime(dateTime: LocalDateTime): String {
    return if (dateTime.toLocalDate() == LocalDate.now()) {
        dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatOverviewItemPreview() {
    val sampleMessage = BluetoothMessage(
        message = "Hello from sample chat! This is a longer message to test the width constraints.",
        isFromLocalUser = false,
        dateTime = LocalDateTime.now().minusHours(1),
    )
    val sampleOverview = ChatOverview(
        name = "James Alexander Carter",
        address = "00:11:22:33:44:55",
        latestMessage = sampleMessage,
    )
    ChatOverviewItem(chatOverview = sampleOverview, onClick = {}, modifier = Modifier.width(300.dp))
}
