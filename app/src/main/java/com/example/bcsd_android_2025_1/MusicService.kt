package com.example.bcsd_android_2025_1

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uriString = intent?.getStringExtra("music_uri")
        val title = intent?.getStringExtra("music_title") ?: "재생 중"
        if (uriString != null) {
            playMusic(Uri.parse(uriString), title)
        }
        return START_NOT_STICKY
    }

    fun playMusic(uri: Uri, title: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, uri).apply {
            setOnCompletionListener {
                stopSelf()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
            start()
        }
        showNotification(title)
    }

    private fun showNotification(title: String) {
        val notifIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("음악 재생")
            .setContentText(title)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}
