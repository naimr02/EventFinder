package com.naimrlet.eventfinder

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

            EventFinderTheme {
                if (!isLoggedIn) {
                    // Show the LoginScreen if the user is not logged in
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
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
                            onSubmit = { event ->
                                viewModel.addEvent(event) // Save event to Firebase
                                showForm = false
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun LoginScreen(onLoginSuccess: () -> Unit) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val auth = FirebaseAuth.getInstance()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onLoginSuccess() // Notify success
                            } else {
                                // Handle login failure (e.g., show an error message)
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMain(scrollBehavior: TopAppBarScrollBehavior) {
    MediumTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                "UiTM Event Finder",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        scrollBehavior = scrollBehavior // Set scroll behavior here
    )
}

@Composable
fun ScrollContent(viewModel: EventViewModel, innerPadding: PaddingValues) {
    val events by viewModel.events.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(innerPadding) // Apply padding here
    ) {
        items(events) { event ->
            EventCard(event)
        }
    }
}


@Composable
fun EventCard(event: Event) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Username and Faculty Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = event.username,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = event.facultyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { /* Handle menu click */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder for image or shapes (optional)
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) { /* Add image or placeholder here */ }

            Spacer(modifier = Modifier.height(8.dp))

            // Event details: Name, Location, Time, Description
            Text(
                text = event.eventName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = event.locationTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Buttons (More Info and Join Event)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { /* Handle more info click */ }) {
                    Text("More info")
                }
                Button(onClick = { /* Handle join event click */ }) {
                    Text("Join event")
                }
            }
        }
    }
}