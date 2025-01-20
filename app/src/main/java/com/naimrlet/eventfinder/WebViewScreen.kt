package com.naimrlet.eventfinder

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(url: String) {
    val context = LocalContext.current
    AndroidView(
        factory = { WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }},
        update = { it.loadUrl(url) }
    )
}
