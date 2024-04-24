package com.example.gasleakageproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class FloorMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_map)

        val btnBack = findViewById<Button>(R.id.button_back)

        btnBack.setOnClickListener {
            finish()  // Use finish() to close this activity and return to the previous one in the stack
        }
    }
}
