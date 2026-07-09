package com.sendafrica.messeji.ui.screens.backup

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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.sendafrica.messeji.data.repository.MessageRepository
import com.sendafrica.messeji.ui.theme.Primary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onBack: () -> Unit,
    repository: MessageRepository = hiltViewModel(),
    settings: AppSettings = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val prefs by settings.preferences.collectAsState(
        initial = com.sendafrica.messeji.data.AppPreferences()
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nakala Rudufu na Kurejesha", fontWeight = FontWeight.SemiBold) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.Backup,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Nakala yako imesimbwa kwa usalama.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        repository.createBackup()
                        settings.setBackupLastTime(System.currentTimeMillis())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Default.Backup, contentDescription = null)
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Fanya Nakala Rudufu Sasa", fontSize = 16.sp)
            }

            if (prefs.backupLastTime > 0) {
                val dateFormat = SimpleDateFormat("MMMM d, yyyy HH:mm", Locale("sw"))
                Text(
                    text = "Nakala ya mwisho: ${dateFormat.format(Date(prefs.backupLastTime))}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nakala Rudufu Kiotomatiki",
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp
                )
                Switch(
                    checked = prefs.autoBackup,
                    onCheckedChange = { scope.launch { settings.setAutoBackup(it) } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: file picker */ },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = com.sendafrica.messeji.ui.theme.PrimaryDark
                )
            ) {
                Icon(Icons.Default.Restore, contentDescription = null)
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Rejesha kutoka Nakala Rudufu", fontSize = 16.sp)
            }
        }
    }
}
