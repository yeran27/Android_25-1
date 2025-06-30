package com.example.bcsd_android_2025_1

import android.Manifest
import android.content.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), MusicAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var permissionMessage: TextView
    private lateinit var openSettingsButton: Button
    private lateinit var requestPermissionButton: Button
    private lateinit var musicAdapter: MusicAdapter
    private val musicList = mutableListOf<MusicData>()

    private var musicService: MusicService? = null
    private var isBound = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showMusicList()
        else showPermissionUI()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.MusicRecyclerView)
        permissionMessage = findViewById(R.id.text1)
        openSettingsButton = findViewById(R.id.OpenButton)
        requestPermissionButton = findViewById(R.id.RequestButton)

        musicAdapter = MusicAdapter(musicList, this)
        recyclerView.adapter = musicAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.visibility = View.GONE
        permissionMessage.visibility = View.GONE
        openSettingsButton.visibility = View.GONE
        requestPermissionButton.visibility = View.GONE

        if (hasPermission()) showMusicList()
        else showPermissionUI()

        requestPermissionButton.setOnClickListener { requestPermission() }
        openSettingsButton.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPermission()) showMusicList()
    }

    private fun hasPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
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
            MediaStore.Audio.Media._ID,
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
            val idIdx = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleIdx = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistIdx = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationIdx = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
            while (it.moveToNext()) {
                val id = it.getLong(idIdx)
                val title = it.getString(titleIdx) ?: "Unknown"
                val artist = it.getString(artistIdx) ?: "Unknown"
                val duration = it.getLong(durationIdx)
                val contentUri = ContentUris.withAppendedId(uri, id)
                musicList.add(MusicData(title, artist, duration, contentUri))
            }
        }
        musicAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(music: MusicData) {
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra("music_uri", music.uri.toString())
            putExtra("music_title", music.title)
        }
        ContextCompat.startForegroundService(this, intent)
        if (isBound) musicService?.playMusic(music.uri, music.title)
    }
}
