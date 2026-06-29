package com.example.auriapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        actionBar?.hide()
        appContext = applicationContext
        super.onCreate(savedInstanceState)
// Request permissions for Android 6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_CALENDAR
            )
            
            val toRequest = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (toRequest.isNotEmpty()) {
                requestPermissions(toRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }

        setContent {
            App()
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}