package com.example.gasleakageproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                fetchData()
                handler.postDelayed(this, 5000)  // 10000 milliseconds = 10 seconds
            }
        }

        // Start the initial fetchData operation immediately
        handler.post(runnable)

        val btnFloorMap = findViewById<ImageButton>(R.id.button1)
        val btnHistoricAnalytics = findViewById<ImageButton>(R.id.button2)
        val btnTraining = findViewById<ImageButton>(R.id.button3)
        val btnToggleLeak = findViewById<Button>(R.id.button4)

        btnHistoricAnalytics.setOnClickListener {

            startActivity(Intent(this@MainActivity, activity_historic::class.java))
        }

        btnFloorMap.setOnClickListener {

            startActivity(Intent(this@MainActivity, FloorMap::class.java))
        }

        btnTraining.setOnClickListener {

            startActivity(Intent(this@MainActivity, TrainingActivity::class.java))
        }

        btnToggleLeak.setOnClickListener{
            toggleLeakState()
        }
    }
    private fun fetchData() {
        thread {
            val url = URL("https://api.thingspeak.com/channels/2504476/feeds.json?api_key=PW9GJLSMSUBE32QL&results=1")
            (url.openConnection() as? HttpURLConnection)?.run {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    val response = it.readText()
                    val jsonResponse = JSONObject(response)
                    val feeds = jsonResponse.getJSONArray("feeds")
                    val latestData = feeds.getJSONObject(0)

                    runOnUiThread {
                        findViewById<TextView>(R.id.tvTemperature).text = "Temperature: ${latestData.getString("field1")}"
                        findViewById<TextView>(R.id.tvHumidity).text = "Humidity: ${latestData.getString("field2")}"
                        findViewById<TextView>(R.id.tvPressure).text = "Battery: ${latestData.getString("field3")}"
                        findViewById<TextView>(R.id.tvLeakage).text = "Leakage: ${latestData.getString("field4")}"

                        val leakageValueString = latestData.getString("field4").trim()
                        val leakageValue = leakageValueString.toFloatOrNull()
                        val leakageStatusTextView = findViewById<TextView>(R.id.tvLeakageStatus)

                        val leakageStatus = if (leakageValue != null && leakageValue > 30) {
                            leakageStatusTextView.setTextColor(resources.getColor(R.color.colorLeakageDetected, null))  // Set text color to red
                            "Leakage Detected"
                        } else {
                            leakageStatusTextView.setTextColor(resources.getColor(R.color.colorNoLeakage, null))  // Set text color to black
                            "No Leakage Detected"
                        }
                        leakageStatusTextView.text = "Leakage Status: $leakageStatus"

                        // Debugging: Output the leakage value to understand what is being parsed
                        Log.d("MainActivity", "Leakage value parsed: $leakageValue")


                        val btnToggleLeak = findViewById<Button>(R.id.button4)
                        val field5 = latestData.optString("field5").trim()
                        val alarmState = field5.ifEmpty { "0" }.toIntOrNull() ?: 0
                        // Set the button text based on the alarm state
                        btnToggleLeak.text = if (alarmState == 1) "Turn Off" else "Turn On"
                    }
                }
            }
        }
    }

    private fun toggleLeakState() {
        thread {
            try {
                // Fetch current state
                val fetchUrl = URL("https://api.thingspeak.com/channels/2504476/feeds.json?api_key=PW9GJLSMSUBE32QL&results=1")
                val currentState = (fetchUrl.openConnection() as? HttpURLConnection)?.let { connection ->
                    connection.requestMethod = "GET"
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        connection.inputStream.bufferedReader().use { reader ->
                            val response = reader.readText()
                            val jsonResponse = JSONObject(response)
                            val feeds = jsonResponse.getJSONArray("feeds")
                            if (feeds.length() > 0) {
                                val fieldData = feeds.getJSONObject(0).optString("field5")
                                if (fieldData.isNullOrEmpty() || fieldData == "null") 0 else fieldData.toInt()
                            } else {
                                0  // Default to 0 if no feeds are available
                            }
                        }
                    } else {
                        throw RuntimeException("Failed to fetch current state, HTTP response code: ${connection.responseCode}")
                    }
                } ?: 0  // Default to 0 if connection fails or is null

                // Toggle state
                val newState = if (currentState == 1) 0 else 1
                val updateUrl = URL("https://api.thingspeak.com/update?api_key=PW9GJLSMSUBE32QL&field5=$newState")
                (updateUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    if (this.responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread {
                            // Update the button text based on the new state
                            findViewById<Button>(R.id.button4).text = if (newState == 1) "Turn Off" else "Turn On"
                        }
                    } else {
                        throw RuntimeException("Failed to update state, HTTP response code: ${this.responseCode}")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)  // Remove callbacks to avoid memory leaks

    }
}