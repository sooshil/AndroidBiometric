package com.sukajee.androidbiometric

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sukajee.androidbiometric.ui.theme.AndroidBiometricTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidBiometricTheme {
                val manager = BiometricPromptManager(this)
                val result by manager.promptResult.collectAsState(null)

                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        println("Activity result is $it")
                    }
                )
                LaunchedEffect(result) {
                    if (result is BiometricPromptManager.BioMetricResult.AuthenticationNotSet) {
                        if (Build.VERSION.SDK_INT >= 30) {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )
                            }
                            enrollLauncher.launch(enrollIntent)
                        }
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(0xFF3e3e4c))
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            manager.showBiometricPrompt(
                                title = "Biometric Auth",
                                description = "Scan your finger or face"
                            )
                        }) {
                            Text(text = "Start Authentication")
                        }
                        result?.let {
                            Text(
                                text = when (it) {
                                    is BiometricPromptManager.BioMetricResult.AuthenticationError -> it.error
                                    BiometricPromptManager.BioMetricResult.AuthenticationFailed -> "Authentication Failed"
                                    BiometricPromptManager.BioMetricResult.AuthenticationNotSet -> "Authentication not set"
                                    BiometricPromptManager.BioMetricResult.AuthenticationSuccess -> "Authentication Success"
                                    BiometricPromptManager.BioMetricResult.FeatureNotAvailable -> "Authentication not available"
                                    BiometricPromptManager.BioMetricResult.HardwareUnavailable -> "Hardware not available"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}