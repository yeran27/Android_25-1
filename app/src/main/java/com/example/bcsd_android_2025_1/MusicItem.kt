package com.example.bcsd_android_2025_1

import android.net.Uri

data class MusicData(
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: Uri
)