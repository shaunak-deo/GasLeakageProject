package com.example.gasleakageproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button

class activity_historic : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic)

        val urls = listOf(
            "https://thingspeak.com/channels/2504476/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Humidity&type=line",
            "https://thingspeak.com/channels/2504476/charts/2?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Temperature&type=line",
            "https://thingspeak.com/channels/2504476/charts/3?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Battery&type=line",
            "https://thingspeak.com/channels/2504476/charts/4?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Leakage&type=line"
        )

        urls.forEachIndexed { index, url ->
            val webViewId = resources.getIdentifier("webview${index + 1}", "id", packageName)
            val webView = findViewById<WebView>(webViewId)
            webView.webViewClient = WebViewClient()
            webView.apply {
                loadUrl(url)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
            }
        }

//        val webView = findViewById<WebView>(R.id.webview1)
//        webView.webViewClient = WebViewClient()
//        webView.apply {
//            loadUrl("https://thingspeak.com/channels/2504476/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15")
//            settings.javaScriptEnabled = true
//            settings.domStorageEnabled = true
//        }

        val btnBack = findViewById<Button>(R.id.button_back2)
        btnBack.setOnClickListener {
            finish()  // Use finish() to close this activity and return to the previous one in the stack
        }
    }
}
