# BluetoothChat

A modern Android chat application that enables direct communication between devices using Bluetooth technology, without requiring an internet
connection.

## 📱 Features

- **Device Discovery**: Find nearby Bluetooth devices
- **Real-time Messaging**: Send and receive messages in real-time
- **Persistent Chats**: Message history is stored locally for each device
- **Profile Management**: View and manage device profiles
- **Modern UI**: Built with Material 3 design principles

## 🛠️ Technology Stack

- Kotlin
- Jetpack Compose
- Navigation Compose
- Room Database
- Dagger Hilt
- Bluetooth API

## 📋 Requirements

- Android SDK 30 or higher (Android 12+)
- Bluetooth-enabled device
- Android Studio Giraffe or newer

## ⚙️ Setup & Installation

1. Clone the repository:

```bash
git clone https://github.com/knivram/BluetoothChat.git
```

2. Open the project in Android Studio

3. Build the project:

```bash
./gradlew build
```

4. Run the app on your device or emulator:

```bash
./gradlew installDebug
```

5. Run linter after some changes:

```bash
./gradlew ktlintCheck
```

## 📚 Project Structure

The app follows a clean architecture approach with the following main components:

- **data**: Contains implementations of repositories and data sources
    - **bluetooth**: Bluetooth controller implementation
    - **local**: Room database and DAO implementations

- **di**: Dependency injection modules

- **domain**: Business logic and models
    - **chat**: Contains data models and interfaces for chat functionality

- **presentation**: UI components
    - **components**: Reusable UI components
    - **screens**: Main app screens (Chats Overview, Chat, Profile)
    - **viewmodels**: ViewModels for each screen

- **ui**: Theme and styling

## 📱 Usage

1. Launch the app
2. Allow requested Bluetooth and notification permissions
3. From the Chats screen, tap the add button to discover nearby devices
4. Select a device to start a chat
5. Send messages using the text field at the bottom of the screen

### 🚨 Known issues

Paring new phones can make some problems. An easy fix for that is to just use the settings app to start the pairing process. After that both devices will have eachother in the paired devices list and can direcly start chatting.


## 🔒 Permissions

The app requires the following permissions:

- `BLUETOOTH_SCAN`: To search for nearby Bluetooth devices
- `BLUETOOTH_CONNECT`: To connect to Bluetooth devices
- `POST_NOTIFICATIONS`: For chat notifications (Android 13+)
