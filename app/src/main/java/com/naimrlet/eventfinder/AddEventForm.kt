package com.naimrlet.eventfinder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddEventForm(
    onDismiss: () -> Unit,
    onSubmit: (Event) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var facultyName by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var locationTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Event") },
        text = {
            Column {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = facultyName,
                    onValueChange = { facultyName = it },
                    label = { Text("Faculty Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = locationTime,
                    onValueChange = { locationTime = it },
                    label = { Text("Location & Time") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (username.isNotBlank() && eventName.isNotBlank()) {
                    val newEvent =
                        Event(username, facultyName, eventName, locationTime, description)
                    onSubmit(newEvent)
                } else {
                    // Optional: Show error message for missing required fields
                }
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
