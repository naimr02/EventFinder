package com.naimrlet.eventfinder

import Event
import QRScanner
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.naimrlet.eventfinder.ui.theme.EventFinderTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EventViewModel by viewModels()
            var isLoggedIn by remember { mutableStateOf(false) }
            var showSignUp by remember { mutableStateOf(false) }
            var showQRScanner by remember { mutableStateOf(false) } // State to control QRScanner visibility
            var scannedUrl by remember { mutableStateOf("") } // Store scanned URL
            var showWebView by remember { mutableStateOf(false) } // State to control WebView visibility

            EventFinderTheme {
                if (!isLoggedIn) {
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onSignUpClick = { showSignUp = true }
                    )
                } else {
                    Scaffold(
                        topBar = {
                            TopBarMain(
                                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                                onLogoutClick = { isLoggedIn = false },
                                onQRScanClick = {
                                    // Toggle QR Scanner visibility
                                    showQRScanner = !showQRScanner
                                    if (showQRScanner) {
                                        showWebView = false // Hide WebView when opening QR Scanner
                                    }
                                }
                            )
                        },
                        content = { innerPadding ->
                            when {
                                showQRScanner -> {
                                    QRScanner(onQRCodeScanned = { url ->
                                        showQRScanner = false // Hide QR scanner after scanning
                                        scannedUrl = url
                                        showWebView = true // Show WebView with scanned URL
                                    })
                                }
                                showWebView -> {
                                    WebViewScreen(url = scannedUrl)
                                }
                                else -> {
                                    ScrollContent(viewModel, innerPadding)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScrollContent(viewModel: EventViewModel, innerPadding: PaddingValues) {
    val events by viewModel.events.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(innerPadding)
    ) {
        items(events) { event ->
            EventCard(event, onDelete = { viewModel.deleteEvent(it) })
        }
    }
}


