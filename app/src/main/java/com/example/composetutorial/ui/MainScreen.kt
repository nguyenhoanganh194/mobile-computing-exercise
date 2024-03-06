@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.composetutorial.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.composetutorial.ui.features.CameraScreen
import com.example.composetutorial.ui.features.NoPermissionScreen

@Composable
fun MainScreen(onNavigateBack : (String?)->Unit) {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onNavigateBack: (String?)->Unit
) {

    if (hasPermission) {
        CameraScreen(onNavigateBack)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}

