package com.example.bluetooth_chat.domain.chat

import java.io.IOException

class TransferFailedException: IOException("Reading incoming message failed")