package com.example.gasleakageproject

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
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
                handler.postDelayed(this, 10000)  // 10000 milliseconds = 10 seconds
            }
        }

        // Start the initial fetchData operation immediately
        handler.post(runnable)
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
                        findViewById<TextView>(R.id.tvPressure).text = "Pressure: ${latestData.getString("field3")}"
                        findViewById<TextView>(R.id.tvLeakage).text = "Leakage: ${latestData.getString("field4")}"
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)  // Remove callbacks to avoid memory leaks
    }
}
