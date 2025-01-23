package com.naimrlet.eventfinder

import com.naimrlet.eventfinder.event.Event
import com.naimrlet.eventfinder.home.QRScanner
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.naimrlet.eventfinder.auth.LoginScreen
import com.naimrlet.eventfinder.auth.SignUpScreen
import com.naimrlet.eventfinder.event.AddEventButton
import com.naimrlet.eventfinder.event.AddEventForm
import com.naimrlet.eventfinder.event.EventCard
import com.naimrlet.eventfinder.event.EventViewModel
import com.naimrlet.eventfinder.home.TopBarMain
import com.naimrlet.eventfinder.home.WebViewScreen
import com.naimrlet.eventfinder.ui.theme.EventFinderTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EventViewModel by viewModels()
            var isLoggedIn by remember { mutableStateOf(false) }
            var showForm by remember { mutableStateOf(false) }
            var showSignUp by remember { mutableStateOf(false) }
            var showQRScanner by remember { mutableStateOf(false) }
            var scannedUrl by remember { mutableStateOf("") }
            var showWebView by remember { mutableStateOf(false) }
            var eventAdded by remember { mutableStateOf(false) }

            EventFinderTheme {
                if (!isLoggedIn) {
                    if (showSignUp) {
                        SignUpScreen(
                            onSignUpSuccess = {
                                isLoggedIn = true
                                showSignUp = false
                            },
                            onBackToLogin = {
                                showSignUp = false // Navigate back to Login Screen
                            }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true },
                            onSignUpClick = { showSignUp = true }
                        )
                    }
                } else {
                    // Handle back navigation when logged in
                    BackHandler(enabled = true) {
                        if (showQRScanner) {
                            showQRScanner = false // Close QR Scanner
                        } else if (showForm) {
                            showForm = false // Close Event Form
                        } else if (showWebView) {
                            showWebView = false // Close WebView
                        } else {
                            isLoggedIn = false // Log out and return to Login Screen
                        }
                    }

                    Scaffold(
                        topBar = {
                            TopBarMain(
                                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                                onLogoutClick = { isLoggedIn = false },
                                onQRScanClick = {
                                    showQRScanner = !showQRScanner
                                    if (showQRScanner) {
                                        showWebView = false // Hide WebView when opening QR Scanner
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            AddEventButton(onClick = { showForm = true })
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

                    if (showForm) {
                        AddEventForm(
                            onDismiss = { showForm = false },
                            onSubmit = { event: Event ->
                                viewModel.addEvent(event)
                                eventAdded = true // Set flag to trigger notification

                                // Show notification for the newly added event.
                                showNotification(
                                    context = this@MainActivity,
                                    title = "New Event Added",
                                    body = "New event has been successfully added! Check out NoMeritGo ASAP!"
                                )

                                showForm = false // Close the form after submission.
                            }
                        )
                    }

                    if (eventAdded) {
                        eventAdded = false // Reset state after showing notification.
                    }
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
