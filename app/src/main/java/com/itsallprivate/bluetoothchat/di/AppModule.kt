package com.itsallprivate.bluetoothchat.di

import android.content.Context
import androidx.room.Room
import com.itsallprivate.bluetoothchat.data.bluetooth.BluetoothControllerImpl
import com.itsallprivate.bluetoothchat.data.local.ChatDatabase
import com.itsallprivate.bluetoothchat.data.local.ChatRepositoryImpl
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothController
import com.itsallprivate.bluetoothchat.domain.chat.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return BluetoothControllerImpl(context)
    }

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "chat_database",
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatRepository(database: ChatDatabase): ChatRepository {
        return ChatRepositoryImpl(database.chatDeviceDao(), database.chatMessageDao())
    }
}
