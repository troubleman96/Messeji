package com.sendafrica.messeji.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sendafrica.messeji.ui.navigation.Screen
import com.sendafrica.messeji.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mipangilio", fontWeight = FontWeight.SemiBold) },
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
        ) {
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Arifa",
                onClick = { onNavigate(Screen.SettingsNotifications) }
            )
            SettingsItem(
                icon = Icons.Default.ColorLens,
                title = "Muonekano",
                onClick = { onNavigate(Screen.SettingsAppearance) }
            )
            SettingsItem(
                icon = Icons.Default.SimCard,
                title = "Kadi za SIM",
                onClick = { onNavigate(Screen.SettingsSim) }
            )
            SettingsItem(
                icon = Icons.Default.Block,
                title = "Namba Zilizozuiwa",
                onClick = { onNavigate(Screen.SettingsBlocked) }
            )
            SettingsItem(
                icon = Icons.Default.Backup,
                title = "Nakala Rudufu na Kurejesha",
                onClick = { onNavigate(Screen.SettingsBackup) }
            )
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Ulinzi wa Programu",
                onClick = { onNavigate(Screen.SettingsLock) }
            )
            SettingsItem(
                icon = Icons.Default.Storage,
                title = "Data na Hifadhi",
                onClick = { onNavigate(Screen.SettingsData) }
            )
            SettingsItem(
                icon = Icons.Default.CameraAlt,
                title = "Unganisha na Kompyuta",
                subtitle = "Inakuja Hivi Karibuni",
                onClick = { }
            )
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Kuhusu",
                onClick = { onNavigate(Screen.SettingsAbout) }
            )
            SettingsItem(
                icon = Icons.Default.Help,
                title = "Msaada",
                onClick = { onNavigate(Screen.SettingsHelp) }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Default.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
