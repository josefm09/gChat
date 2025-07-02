package com.example.gchat // Or your actual package name

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChatWebView(
    url: String,
    modifier: Modifier = Modifier,
    onPageStarted: (url: String?, favicon: Bitmap?) -> Unit = { _, _ -> },
    onPageFinished: (url: String?) -> Unit = { _ -> },
    onError: (errorCode: Int, description: CharSequence?, failingUrl: String?) -> Unit = { _, _, _ -> }
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            onPageStarted(url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            onPageFinished(url)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            request?.url?.let { view?.loadUrl(it.toString()) }
                            return true
                        }

                        // Make sure your onReceivedError is using the correct parameters
                        // The original one in your context had `description: String` but it should be `description: CharSequence?`
                        // and `failingUrl: String` should be `failingUrl: String?`
                        override fun onReceivedError(
                            view: WebView,
                            errorCode: Int,
                            description: String,
                            failingUrl: String
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl) // Pass view here
                            isLoading = false
                            onError(errorCode, description, failingUrl)
                        }
                    }
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    // --- MODIFIED USER AGENT ---
                    settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
                    // --- END MODIFICATION ---

                    loadUrl(url)
                }
            },
            update = { webView ->
                // If the URL could change, you might need to check if the current
                // webView.url is different from the new `url` and call loadUrl(url)
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}