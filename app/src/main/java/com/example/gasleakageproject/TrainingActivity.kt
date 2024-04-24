package com.example.gasleakageproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button

class TrainingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        val btnBack = findViewById<Button>(R.id.button_back3)
        val webView1 = findViewById<WebView>(R.id.webview1)
        val webView2 = findViewById<WebView>(R.id.webview2)
//        val webView = findViewById<WebView>(R.id.webview)

        webView1.apply {
            webChromeClient = WebChromeClient()  // For handling video playback
            settings.javaScriptEnabled = true  // Enable JavaScript

            // Correctly formatted embed URL for the YouTube video
            val videoUrl = "https://www.youtube.com/embed/1zB7tMH9k_s?autoplay=1&controls=1&showinfo=0&rel=0"
            loadUrl(videoUrl)
        }

        webView2.apply {
            webChromeClient = WebChromeClient()  // For handling video playback
            settings.javaScriptEnabled = true  // Enable JavaScript

            // Correctly formatted embed URL for the YouTube video
            val videoUrl = "https://www.youtube.com/embed/hRzatAPzyN4?autoplay=1&controls=1&showinfo=0&rel=0"

            loadUrl(videoUrl)
        }
//
//        webView.apply {
//            webViewClient = WebViewClient()  // Ensures links open within the WebView
//            loadUrl("https://www.youtube.com/watch?v=1zB7tMH9k_s&ab_channel=HAZWOPER-OSHATraining") // Replace VIDEO_ID with your YouTube video ID
//        }

        btnBack.setOnClickListener {
            finish()  // Use finish() to close this activity and return to the previous one in the stack
        }

//        btnBack.setOnClickListener {
//            // Intent to open the YouTube video in a web browser or YouTube app
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=1zB7tMH9k_s"))
//            startActivity(intent)
    }
}
