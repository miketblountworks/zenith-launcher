package com.example.media

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.example.MyNotificationListenerService

object MediaController {
    fun dispatchMediaKey(context: Context, keyCode: Int) {
        try {
            val controller = MyNotificationListenerService.activeController
            if (controller != null) {
                var dispatchSuccess = false
                try {
                    // Try Transport Controls first
                    when (keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                            val state = controller.playbackState?.state
                            if (state == android.media.session.PlaybackState.STATE_PLAYING) {
                                controller.transportControls.pause()
                            } else {
                                controller.transportControls.play()
                            }
                            dispatchSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_PLAY -> {
                            controller.transportControls.play()
                            dispatchSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                            controller.transportControls.pause()
                            dispatchSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            controller.transportControls.skipToNext()
                            dispatchSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            controller.transportControls.skipToPrevious()
                            dispatchSuccess = true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (dispatchSuccess) {
                    return
                }

                // ALWAYS dispatch direct media button keyEvents to the target session as a companion/fallback.
                // This ensures all streaming apps (like YouTube, YouTube Music, Spotify etc.) process play/pause immediately.
                try {
                    val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
                    val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
                    val d1 = controller.dispatchMediaButtonEvent(downEvent)
                    val u1 = controller.dispatchMediaButtonEvent(upEvent)
                    dispatchSuccess = d1 || u1
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (dispatchSuccess) {
                    return
                }

                // If direct session dispatching returned false/failed, we apply System AudioManager.
                try {
                    val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
                    val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
                    am.dispatchMediaKeyEvent(downEvent)
                    am.dispatchMediaKeyEvent(upEvent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return
            }
            
            // Absolute global fallback if no active controller bound (e.g., initial startup / no active sessions)
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            am.dispatchMediaKeyEvent(downEvent)
            am.dispatchMediaKeyEvent(upEvent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
