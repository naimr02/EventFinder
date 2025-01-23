package com.naimrlet.eventfinder.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMain(
    scrollBehavior: TopAppBarScrollBehavior,
    onLogoutClick: () -> Unit,
    onQRScanClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("NoMeritGo", maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        actions = {
            // QR Scan Button
            IconButton(onClick = onQRScanClick) { // Trigger state change for QR scanner
                Icon(
                    imageVector = Icons.Default.CameraAlt, // Use a camera icon
                    contentDescription = "Scan QR Code"
                )
            }
            // Logout Button
            IconButton(onClick = onLogoutClick) { // Handle logout logic
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
