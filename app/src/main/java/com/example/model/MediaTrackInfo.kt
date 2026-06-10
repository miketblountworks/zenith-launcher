package com.example.model

import android.graphics.Bitmap

data class MediaTrackInfo(
    val title: String,
    val artist: String,
    val packageName: String,
    val isPlaying: Boolean,
    val durationMs: Long = 180000L,
    val progressMs: Long = 0L,
    val artwork: Bitmap? = null
)
