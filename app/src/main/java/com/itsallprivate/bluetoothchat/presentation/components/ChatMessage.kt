package com.itsallprivate.bluetoothchat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import com.itsallprivate.bluetoothchat.ui.theme.BluetoothChatTheme
import com.itsallprivate.bluetoothchat.ui.theme.OldRose
import com.itsallprivate.bluetoothchat.ui.theme.Vanilla
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 15.dp else 1.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = if (message.isFromLocalUser) 1.dp else 15.dp,
                ),
            )
            .background(
                if (message.isFromLocalUser) OldRose else Vanilla,
            )
            .padding(12.dp),
    ) {
        Text(
            text = message.message,
            color = Color.Black,
            modifier = Modifier.widthIn(min = 80.dp, max = 250.dp),
        )
        Text(
            text = message.dateTime.formatToMessage(),
            fontSize = 10.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

fun LocalDateTime.formatToMessage(): String {
    val date = this.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    val time = this.format(DateTimeFormatter.ofPattern("HH:mm"))
    return "$date at $time"
}

@Preview
@Composable
fun ChatMessagePreview() {
    BluetoothChatTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ChatMessage(
                message = BluetoothMessage(
                    message = "Hi",
                    isFromLocalUser = true,
                    dateTime = LocalDateTime.now().minusDays(1),
                ),
            )
            ChatMessage(
                message = BluetoothMessage(
                    message = "Hello, this is a longer message to test the width constraints.",
                    isFromLocalUser = true,
                    dateTime = LocalDateTime.now(),
                ),
            )
        }
    }
}
