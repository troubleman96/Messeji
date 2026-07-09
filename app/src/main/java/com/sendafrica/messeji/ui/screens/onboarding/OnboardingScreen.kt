package com.sendafrica.messeji.ui.screens.onboarding

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.ui.theme.Accent
import com.sendafrica.messeji.ui.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    settings: AppSettings = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(0) }
    val prefs by settings.preferences.collectAsStateWithLifecycle(
        initialValue = com.sendafrica.messeji.data.AppPreferences()
    )

    val steps = listOf(
        OnboardingStep.Welcome,
        OnboardingStep.SmsPermission,
        OnboardingStep.DefaultApp,
        OnboardingStep.ContactsPermission,
        OnboardingStep.NotificationsPermission,
        OnboardingStep.AppLock,
        OnboardingStep.Complete
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentStep,
            label = "onboarding_step"
        ) { step ->
            when (steps[step]) {
                OnboardingStep.Welcome -> WelcomeStep(onNext = { currentStep = 1 })
                OnboardingStep.SmsPermission -> SmsPermissionStep(
                    onNext = { currentStep = 2 },
                    onSkip = { currentStep = 2 }
                )
                OnboardingStep.DefaultApp -> DefaultAppStep(
                    onNext = { currentStep = 3 },
                    onSkip = { currentStep = 3 },
                    context = context
                )
                OnboardingStep.ContactsPermission -> ContactsPermissionStep(
                    onNext = { currentStep = 4 },
                    onSkip = { currentStep = 4 }
                )
                OnboardingStep.NotificationsPermission -> NotificationsPermissionStep(
                    onNext = { currentStep = 5 },
                    onSkip = { currentStep = 5 }
                )
                OnboardingStep.AppLock -> AppLockStep(
                    settings = settings,
                    onNext = {
                        scope.launch {
                            settings.setOnboardingComplete(true)
                        }
                        currentStep = 6
                    },
                    onSkip = {
                        scope.launch {
                            settings.setOnboardingComplete(true)
                        }
                        currentStep = 6
                    }
                )
                OnboardingStep.Complete -> CompleteStep(
                    onFinish = {
                        scope.launch {
                            settings.setOnboardingComplete(true)
                        }
                        onComplete()
                    }
                )
            }
        }

        // Progress dots
        if (currentStep < steps.size - 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == currentStep) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index <= currentStep) Primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

enum class OnboardingStep {
    Welcome, SmsPermission, DefaultApp, ContactsPermission,
    NotificationsPermission, AppLock, Complete
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Karibu Messeji",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Meseji zako, kwa lugha yako, kwa urahisi.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Anza", fontSize = 18.sp)
        }
    }
}

@Composable
private fun OnboardingPermissionCard(
    title: String,
    body: String,
    onAllow: () -> Unit,
    onSkip: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onAllow,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Ruhusu", fontSize = 18.sp)
        }
        if (onSkip != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onSkip) {
                Text("Ruka kwa sasa", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SmsPermissionStep(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingPermissionCard(
        title = "Ruhusa ya Kusoma na Kutuma Meseji",
        body = "Messeji inahitaji ruhusa ya kusoma na kutuma meseji ili iwe programu yako kuu ya SMS.",
        onAllow = onNext,
        onSkip = null
    )
}

@Composable
private fun DefaultAppStep(onNext: () -> Unit, onSkip: () -> Unit, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weka kama SMS Kuu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Messeji inahitaji kuwa programu yako kuu ya meseji ili kufanya kazi vizuri.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                    context.startActivity(intent)
                }
                onNext()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Weka Kuu", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onSkip) {
            Text("Ruka kwa sasa", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ContactsPermissionStep(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingPermissionCard(
        title = "Ruhusa ya Anwani",
        body = "Ili kuonyesha majina ya watu badala ya namba za simu.",
        onAllow = onNext,
        onSkip = onSkip
    )
}

@Composable
private fun NotificationsPermissionStep(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingPermissionCard(
        title = "Ruhusa ya Arifa",
        body = "Ili kukujulisha ukipokea meseji mpya.",
        onAllow = onNext,
        onSkip = onSkip
    )
}

@Composable
private fun AppLockStep(
    settings: AppSettings,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var lockEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ulinzi wa Programu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Washa Ulinzi wa PIN/Kidole",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = lockEnabled,
                onCheckedChange = { lockEnabled = it }
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                scope.launch {
                    settings.setLockEnabled(lockEnabled)
                }
                onNext()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Endelea", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = {
            scope.launch { settings.setLockEnabled(false) }
            onSkip()
        }) {
            Text("Ruka", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CompleteStep(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Umekamilika!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Messeji iko tayari kutumika. Unaweza kuanza kutuma na kupokea meseji.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Fungua Messeji", fontSize = 18.sp)
        }
    }
}
