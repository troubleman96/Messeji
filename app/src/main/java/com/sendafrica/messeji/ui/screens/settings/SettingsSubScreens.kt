package com.sendafrica.messeji.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.ui.theme.Primary
import kotlinx.coroutines.launch

object SettingsSubScreens {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NotificationsScreen(
        onBack: () -> Unit,
        settings: AppSettings = hiltViewModel()
    ) {
        val scope = rememberCoroutineScope()
        val prefs by settings.preferences.collectAsState(
            initial = com.sendafrica.messeji.data.AppPreferences()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Arifa", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                ToggleRow(
                    title = "Washa Arifa",
                    checked = prefs.notificationsEnabled,
                    onCheckedChange = { scope.launch { settings.setNotificationsEnabled(it) } }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ToggleRow(
                    title = "Mtu kwa Mtu",
                    checked = prefs.notifMtuKwaMtu,
                    onCheckedChange = { scope.launch { settings.setNotifCategory("mtu_kwa_mtu", it) } }
                )
                ToggleRow(
                    title = "Pesa na OTP",
                    checked = prefs.notifPesaOtp,
                    onCheckedChange = { scope.launch { settings.setNotifCategory("pesa_otp", it) } }
                )
                ToggleRow(
                    title = "Matangazo",
                    checked = prefs.notifMatangazo,
                    onCheckedChange = { scope.launch { settings.setNotifCategory("matangazo", it) } }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ToggleRow(
                    title = "Mtetemo",
                    checked = prefs.notifVibrate,
                    onCheckedChange = { scope.launch { settings.setNotifVibrate(it) } }
                )
                ToggleRow(
                    title = "Onyesha kwenye skrini iliyofungwa",
                    checked = prefs.notifShowLockscreen,
                    onCheckedChange = { scope.launch { settings.setNotifShowLockscreen(it) } }
                )
                ToggleRow(
                    title = "Onyesha Pesa na OTP kwenye skrini iliyofungwa",
                    subtitle = "Zima kwa usalama zaidi",
                    checked = prefs.notifShowLockscreenPesaOtp,
                    onCheckedChange = { scope.launch { settings.setNotifShowLockscreenPesaOtp(it) } }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppearanceScreen(
        onBack: () -> Unit,
        settings: AppSettings = hiltViewModel()
    ) {
        val scope = rememberCoroutineScope()
        val prefs by settings.preferences.collectAsState(
            initial = com.sendafrica.messeji.data.AppPreferences()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Muonekano", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Mandhari ya rangi", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                AppearanceOption("Mwanga", "light", prefs.themeMode) {
                    scope.launch { settings.setThemeMode(it) }
                }
                AppearanceOption("Giza", "dark", prefs.themeMode) {
                    scope.launch { settings.setThemeMode(it) }
                }
                AppearanceOption("Mfumo", "system", prefs.themeMode) {
                    scope.launch { settings.setThemeMode(it) }
                }
            }
        }
    }

    @Composable
    private fun AppearanceOption(
        label: String,
        value: String,
        currentValue: String,
        onSelect: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp)
            Switch(
                checked = currentValue == value,
                onCheckedChange = { if (it) onSelect(value) }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SimScreen(
        onBack: () -> Unit,
        settings: AppSettings = hiltViewModel()
    ) {
        val scope = rememberCoroutineScope()
        val prefs by settings.preferences.collectAsState(
            initial = com.sendafrica.messeji.data.AppPreferences()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kadi za SIM", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("SIM kuu ya kutuma meseji", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("SIM 1", "SIM 2").forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp)
                        Switch(
                            checked = prefs.defaultSimSlot == index + 1,
                            onCheckedChange = { if (it) scope.launch { settings.setDefaultSimSlot(index + 1) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp)
            if (subtitle != null) {
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
