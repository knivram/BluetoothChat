package com.itsallprivate.bluetoothchat.domain.util

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

object AppLifecycleHelper : DefaultLifecycleObserver {
    @Volatile
    private var isForeground: Boolean = true

    init {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        } else {
            Handler(Looper.getMainLooper()).post {
                ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        isForeground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        isForeground = false
    }

    fun isAppInForeground(): Boolean = isForeground
}
