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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var count = 0
        lateinit var countText: TextView
        countText = findViewById(R.id.textView)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val newNumber = data?.getIntExtra("COUNT", 0) ?: 0
                count = newNumber
                countText.text = count.toString()
            }
        }

        val toastButton: Button = findViewById(R.id.button_toast)
        toastButton.setOnClickListener {
            Toast.makeText(this, "Boink", Toast.LENGTH_SHORT).show()
        }

        val countButton: Button = findViewById(R.id.button_count)
        countButton.setOnClickListener{
            count++
            countText.text = count.toString()
        }

        val randomButton: Button = findViewById(R.id.button_random)
        randomButton.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("COUNT", count)
            launcher.launch(intent)
        }
    }
}