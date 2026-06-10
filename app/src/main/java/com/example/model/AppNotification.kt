package com.example.model

import android.service.notification.StatusBarNotification

data class AppNotification(
    val appName: String,
    val text: String,
    val pkg: String,
    val key: String,
    val sbn: StatusBarNotification? = null
)
