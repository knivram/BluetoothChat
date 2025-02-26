package com.itsallprivate.bluetoothchat.domain.chat

import java.io.IOException

class TransferFailedException : IOException("Reading incoming message failed")
class ConnectionClosedException : IOException("Connection closed")
