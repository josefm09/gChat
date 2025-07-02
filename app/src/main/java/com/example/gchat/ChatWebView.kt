package com.example.gchat // Or your actual package name

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
    var webViewInstance: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {
                        // ... (WebViewClient methods remain the same) ...
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            onPageStarted(url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            canGoBack = view?.canGoBack() ?: false
                            onPageFinished(url)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val newUrl = request?.url?.toString()
                            if (newUrl != null && view != null) {
                                view.loadUrl(newUrl)
                            }
                            return true
                        }

                        override fun onReceivedError(
                            view: WebView,
                            errorCode: Int,
                            description: String,
                            failingUrl: String
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            isLoading = false
                            canGoBack = view?.canGoBack() ?: false
                            onError(errorCode, description, failingUrl)
                        }
                    }
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

                    // --- ZOOM SETTINGS ---
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    // --- END ZOOM SETTINGS ---

                    // --- VIEWPORT & OVERVIEW SETTINGS ---
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    // --- END VIEWPORT & OVERVIEW SETTINGS ---

                    loadUrl(url)
                }.also {
                    webViewInstance = it
                }
            },
            update = { webView ->
                webViewInstance = webView
                canGoBack = webView.canGoBack()
                // ... (update logic if url can change) ...
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    webViewInstance?.let { wv ->
        BackHandler(enabled = canGoBack) {
            wv.goBack()
            canGoBack = wv.canGoBack()
        }
    }
}