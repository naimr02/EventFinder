package com.naimrlet.eventfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naimrlet.eventfinder.ui.theme.EventFinderTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventFinderTheme {
                // Create a scroll behavior for the TopAppBar
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // Connect scroll behavior to Scaffold
                    topBar = { TopBarMain(scrollBehavior) }, // Pass scroll behavior to TopBarMain
                    floatingActionButton = {
                        AddEventButton(onClick = { /* Handle FAB click */ })
                    },
                    content = { innerPadding ->
                        ScrollContent(innerPadding) // Pass innerPadding to avoid overlap with FAB and top bar
                    }
                )
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
fun ScrollContent(innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(innerPadding) // Apply padding to avoid overlap with FAB and top bar
    ) {
        items(20) { index -> // Generate 20 EventCards
            EventCard() // Replace text with EventCard
        }
    }
}

@Composable
fun EventCard() {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp) // Make the card span full width and add padding around it.
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Username and Faculty Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Username",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Faculty name",
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

            // Placeholder for image or shapes
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Box(
                        modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    )
                    Box(
                        modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, shape = RectangleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Event details: Name, Location, Time, Description
            Text(text = "Event name", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "Location, Time", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "Description of event", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)

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

@Composable
fun AddEventButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary // Set FAB color here if needed.
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add event")
    }
}

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    EventFinderTheme {
        EventCard()
    }
}
