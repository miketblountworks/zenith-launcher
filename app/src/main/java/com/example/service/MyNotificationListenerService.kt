package com.example.service

import android.content.ComponentName
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.os.BundleCompat
import com.example.model.AppNotification
import com.example.model.MediaTrackInfo
import kotlinx.coroutines.flow.MutableStateFlow

class MyNotificationListenerService : NotificationListenerService() {
    companion object {
        var instance: MyNotificationListenerService? = null
        val notificationsFlow = MutableStateFlow<List<AppNotification>>(emptyList())
        val mediaFlow = MutableStateFlow<MediaTrackInfo?>(null)
        var isConnected = false
        var activeController: MediaController? = null
    }

    private var sessionListener: MediaSessionManager.OnActiveSessionsChangedListener? = null

    override fun onListenerConnected() {
        super.onListenerConnected()
        isConnected = true
        instance = this
        
        try {
            val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
            val componentName = ComponentName(this, MyNotificationListenerService::class.java)
            val listener = MediaSessionManager.OnActiveSessionsChangedListener { _ ->
                updateActiveMedia()
            }
            mediaSessionManager.addOnActiveSessionsChangedListener(listener, componentName)
            sessionListener = listener
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        updateActiveMedia()
        fetchActiveNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isConnected = false
        instance = null
        
        try {
            sessionListener?.let { listener ->
                val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
                mediaSessionManager.removeOnActiveSessionsChangedListener(listener)
            }
            sessionListener = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        activeController?.unregisterCallback(mediaCallback)
        activeController = null
        mediaFlow.value = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        fetchActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        fetchActiveNotifications()
    }

    private val mediaCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateActiveMedia()
        }
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateActiveMedia()
        }
        override fun onSessionDestroyed() {
            updateActiveMedia()
        }
    }

    private fun updateActiveMedia() {
        try {
            val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
            val componentName = ComponentName(this, MyNotificationListenerService::class.java)
            val controllers = mediaSessionManager.getActiveSessions(componentName)
            
            var foundMedia: MediaTrackInfo? = null
            var bestController: MediaController? = null
            
            if (controllers.isNotEmpty()) {
                bestController = controllers.firstOrNull { controller ->
                    val state = controller.playbackState?.state
                    state == PlaybackState.STATE_PLAYING ||
                    state == PlaybackState.STATE_BUFFERING ||
                    state == PlaybackState.STATE_CONNECTING ||
                    state == PlaybackState.STATE_FAST_FORWARDING ||
                    state == PlaybackState.STATE_REWINDING
                } ?: controllers.firstOrNull()
                
                if (bestController != null) {
                    val metadata = bestController.metadata
                    val playbackState = bestController.playbackState
                    
                    val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                        ?: metadata?.getText(MediaMetadata.METADATA_KEY_TITLE)?.toString()
                        ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                        ?: metadata?.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)?.toString()
                        ?: ""
                    val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                        ?: metadata?.getText(MediaMetadata.METADATA_KEY_ARTIST)?.toString()
                        ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                        ?: metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)?.toString()
                        ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
                        ?: metadata?.getText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)?.toString()
                        ?: ""
                    val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
                    val progress = playbackState?.position ?: 0L
                    
                    val isPlaying = playbackState?.state == PlaybackState.STATE_PLAYING || 
                                    playbackState?.state == PlaybackState.STATE_BUFFERING
                    
                    if (title.isNotEmpty()) {
                        val artwork = try {
                            metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                        } catch (e: Exception) {
                            null
                        }
                        foundMedia = MediaTrackInfo(
                            title = title,
                            artist = artist,
                            packageName = bestController.packageName ?: "",
                            isPlaying = isPlaying,
                            durationMs = if (duration > 0) duration else 180000L,
                            progressMs = progress,
                            artwork = artwork
                        )
                    }
                }
            }
            
            if (foundMedia == null) {
                val active = try { activeNotifications } catch (_: Exception) { null }
                if (active != null) {
                    for (sbn in active) {
                        if (sbn == null) continue
                        val pkg = sbn.packageName ?: ""
                        val notification = sbn.notification ?: continue
                        val extras = notification.extras ?: continue
                        
                        val isTransport = notification.category == android.app.Notification.CATEGORY_TRANSPORT ||
                                          extras.containsKey("android.mediaSession") ||
                                          pkg.contains("spotify") || pkg.contains("music") || pkg.contains("youtube") || pkg.contains("vlc") ||
                                          pkg.contains("pandora") || pkg.contains("soundcloud") || pkg.contains("tidal") || pkg.contains("deezer")
                        
                        if (isTransport) {
                            val titleChar = extras.getCharSequence("android.title")
                            val textChar = extras.getCharSequence("android.text")
                            val title = titleChar?.toString() ?: ""
                            val artist = textChar?.toString() ?: ""
                            if (title.isNotEmpty()) {
                                @Suppress("DEPRECATION")
                                val artworkVal = try {
                                    BundleCompat.getParcelable(extras, "android.largeIcon", android.graphics.Bitmap::class.java)
                                        ?: BundleCompat.getParcelable(extras, "android.largeIcon.big", android.graphics.Bitmap::class.java)
                                        ?: (notification.getLargeIcon()?.loadDrawable(this@MyNotificationListenerService) as? android.graphics.drawable.BitmapDrawable)?.bitmap
                                } catch (_: Exception) {
                                    null
                                }
                                foundMedia = MediaTrackInfo(
                                    title = title,
                                    artist = artist,
                                    packageName = pkg,
                                    isPlaying = true,
                                    artwork = artworkVal
                                )
                                break
                            }
                        }
                    }
                }
            }
            
            if (bestController != activeController) {
                activeController?.unregisterCallback(mediaCallback)
                bestController?.registerCallback(mediaCallback)
            }
            
            activeController = bestController
            mediaFlow.value = foundMedia
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchActiveNotifications() {
        try {
            updateActiveMedia()
            val active = activeNotifications ?: return
            val list = active.distinctBy { it.key }.mapNotNull { sbn ->
                if (sbn == null) return@mapNotNull null
                val pkg = sbn.packageName ?: ""
                if (pkg == "com.android.systemui") return@mapNotNull null
                val notification = sbn.notification ?: return@mapNotNull null
                val extras = notification.extras ?: return@mapNotNull null
                val titleCharSeq = extras.getCharSequence("android.title")
                val title = titleCharSeq?.toString() ?: ""
                val textCharSeq = extras.getCharSequence("android.text")
                val text = textCharSeq?.toString() ?: ""
                
                if (pkg.isNotEmpty() && sbn.isClearable && (title.isNotEmpty() || text.isNotEmpty())) {
                    val pm = packageManager
                    val appName = try {
                        val info = pm.getApplicationInfo(pkg, 0)
                        pm.getApplicationLabel(info).toString()
                    } catch (t: Throwable) {
                        pkg
                    }
                    val itemKey = sbn.key ?: ""
                    
                    val combinedText = if (title.isNotEmpty() && text.isNotEmpty() && !text.startsWith(title)) {
                        "$title: $text"
                    } else if (title.isNotEmpty()) {
                        title
                    } else {
                        text
                    }
                    
                    AppNotification(appName, combinedText, pkg, itemKey, sbn)
                } else null
            }
            notificationsFlow.value = list
        } catch (_: Exception) {
            // ignore
        }
    }
}
