package com.itsallprivate.bluetoothchat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 15.dp else 1.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = if (message.isFromLocalUser) 1.dp else 15.dp
                )
            )
            .background(
                if (message.isFromLocalUser) OldRose else Vanilla
            )
            .padding(12.dp)
    ) {
        Text(
            text = message.senderName,
            fontSize = 10.sp,
            color = Color.Black,
        )
        Text(
            text = message.message,
            color = Color.Black,
            modifier = Modifier.widthIn(min= 80.dp, max = 250.dp)
        )

    }
}

@Preview
@Composable
fun ChatMessagePreview() {
    BluetoothChatTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChatMessage(
                message = BluetoothMessage(
                    message = "Hi",
                    senderName = "Pixel 8",
                    isFromLocalUser = true,
                )
            )
            ChatMessage(
                message = BluetoothMessage(
                    message = "Hello, this is a longer message to test the width constraints.",
                    senderName = "Pixel 8",
                    isFromLocalUser = true,
                )
            )
        }
    }
}