package com.example.gchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gchat.ui.theme.GChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GChatTheme {
                // The Google Chat URL
                // Note: Users will likely need to sign in within the WebView
                val googleChatUrl = "https://chat.google.com"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatWebView(
                        url = googleChatUrl,
                        modifier = Modifier.padding(innerPadding),
                        onPageStarted = { url, _ ->
                            Log.i("ChatWebView", "Page started loading: $url")
                        },
                        onPageFinished = { url ->
                            Log.i("ChatWebView", "Page finished loading: $url")
                        },
                        onError = { errorCode, description, failingUrl ->
                            Log.e("ChatWebView", "Error loading page: $failingUrl, Code: $errorCode, Desc: $description")
                            // You could show a user-facing error message here
                        }
                    )
                }
            }
        }
    }
}

// You can keep or remove the Greeting and GreetingPreview if you no longer need them
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GChatTheme {
        Greeting("Android")
    }
}