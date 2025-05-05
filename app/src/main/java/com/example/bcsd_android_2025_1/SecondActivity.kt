package com.example.bcsd_android_2025_1
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val count = intent.getIntExtra("COUNT", 0)
        val random = (0 .. 10).random()
        val randomButton = findViewById<Button>(R.id.button_random)
        randomButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("COUNT", random)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}