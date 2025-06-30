package com.example.bcsd_android_2025_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var permissionMessage: TextView
    private lateinit var openSettingsButton: Button
    private lateinit var requestPermissionButton: Button

    private lateinit var musicAdapter: MusicAdapter
    private val musicList = mutableListOf<MusicData>()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showMusicList()
        } else {
            showPermissionUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.MusicRecyclerView)
        permissionMessage = findViewById(R.id.text1)
        openSettingsButton = findViewById(R.id.OpenButton)
        requestPermissionButton = findViewById(R.id.RequestButton)

        musicAdapter = MusicAdapter(musicList)
        recyclerView.adapter = musicAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.visibility = View.GONE
        permissionMessage.visibility = View.GONE
        openSettingsButton.visibility = View.GONE
        requestPermissionButton.visibility = View.GONE

        if (hasPermission()) {
            showMusicList()
        } else {
            showPermissionUI()
        }

        openSettingsButton.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        requestPermissionButton.setOnClickListener {
            requestPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPermission()) {
            showMusicList()
        }
    }

    private fun hasPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)
    }

    private fun showPermissionUI() {
        recyclerView.visibility = View.GONE
        permissionMessage.visibility = View.VISIBLE
        openSettingsButton.visibility = View.VISIBLE
        requestPermissionButton.visibility = View.VISIBLE
    }

    private fun showMusicList() {
        recyclerView.visibility = View.VISIBLE
        permissionMessage.visibility = View.GONE
        openSettingsButton.visibility = View.GONE
        requestPermissionButton.visibility = View.GONE

        loadMusicList()
    }

    private fun loadMusicList() {
        musicList.clear()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        val cursor = contentResolver.query(
            uri,
            projection,
            MediaStore.Audio.Media.IS_MUSIC + "!=0",
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )

        cursor?.use {
            val titleIdx = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistIdx = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationIdx = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val title = if (titleIdx >= 0) it.getString(titleIdx) ?: "Unknown" else "Unknown"
                val artist = if (artistIdx >= 0) it.getString(artistIdx) ?: "Unknown" else "Unknown"
                val duration = if (durationIdx >= 0) it.getLong(durationIdx) else 0L

                musicList.add(MusicData(title, artist, duration))
            }
        }

        musicAdapter.notifyDataSetChanged()
    }
}
