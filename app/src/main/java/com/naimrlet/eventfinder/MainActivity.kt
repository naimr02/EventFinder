package com.naimrlet.eventfinder

import Event
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
            var showForm by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(false) }
            var showSignUp by remember { mutableStateOf(false) } // Add state to show SignUpScreen

            EventFinderTheme {
                if (!isLoggedIn) {
                    if (showSignUp) {
                        // Show the SignUpScreen
                        SignUpScreen(onSignUpSuccess = {
                            isLoggedIn = true
                            showSignUp = false
                        })
                    } else {
                        // Show the LoginScreen
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true },
                            onSignUpClick = {
                                showSignUp = true
                            } // Handle navigation to SignUpScreen
                        )
                    }
                } else {
                    // Show the main content of your app if logged in
                    Scaffold(
                        topBar = { TopBarMain(TopAppBarDefaults.enterAlwaysScrollBehavior()) },
                        floatingActionButton = {
                            AddEventButton(onClick = { showForm = true })
                        },
                        content = { innerPadding ->
                            ScrollContent(viewModel, innerPadding)
                        }
                    )

                    if (showForm) {
                        AddEventForm(
                            onDismiss = { showForm = false },
                            onSubmit = { event: Event ->
                                viewModel.addEvent(event) // Save event to Firebase
                                showForm = false
                            }
                        )
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


