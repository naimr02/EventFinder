package com.naimrlet.eventfinder.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun EventCard(event: Event, onDelete: (Event) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var showMoreInfo by remember { mutableStateOf(false) } // State for showing the dialog

    val locationLatLng: LatLng? =
        event.location.takeIf { it.isNotBlank() }?.let { location ->
            val cleanedLocation = location.replace("lat/lng: (", "").replace(")", "")
            val parts = cleanedLocation.split(",").mapNotNull { it.trim().toDoubleOrNull() }
            if (parts.size == 2) LatLng(parts[0], parts[1]) else null
        }

    val cameraPositionState = rememberCameraPositionState {
        position = locationLatLng?.let {
            CameraPosition.fromLatLngZoom(it, 12f)
        } ?: CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Section: Username and Faculty Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = event.username, style = MaterialTheme.typography.bodyLarge)
                    Text(text = event.facultyName, style = MaterialTheme.typography.bodySmall)
                }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expanded = false
                                onDelete(event)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google Maps Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                if (locationLatLng != null) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(state = MarkerState(position = locationLatLng))
                    }
                } else {
                    Text(
                        text = "No Location Available",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Event Details Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = event.eventName, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = { showMoreInfo = true }) { // Show the dialog
                    Text("More Info")
                }
                Button(onClick = { /* Handle Join Event */ }) {
                    Text("Join Event")
                }
            }
        }
    }

    // Dialog for More Info
    if (showMoreInfo) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showMoreInfo = false },
            title = {
                Text(text = "Program Details")
            },
            text = {
                Text(text = event.description) // Display the description here
            },
            confirmButton = {
                Button(onClick = { showMoreInfo = false }) {
                    Text("Close")
                }
            }
        )
    }
}