package com.sendafrica.messeji.ui.screens.lock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.ui.theme.Alert
import com.sendafrica.messeji.ui.theme.Primary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockSetupScreen(
    onBack: () -> Unit,
    settings: AppSettings = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val prefs by settings.preferences.collectAsState(
        initial = com.sendafrica.messeji.data.AppPreferences()
    )
    var lockEnabled by remember { mutableStateOf(prefs.lockEnabled) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var timeout by remember { mutableStateOf(prefs.lockTimeout) }
    var biometricEnabled by remember { mutableStateOf(prefs.biometricEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ulinzi wa Programu", fontWeight = FontWeight.SemiBold) },
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
            SwitchRow(
                title = "Washa Ulinzi wa PIN/Kidole",
                checked = lockEnabled,
                onCheckedChange = { lockEnabled = it }
            )

            if (lockEnabled) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; pinError = null },
                    label = { Text("Weka nenosiri la tarakimu 4–6") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        errorBorderColor = Alert,
                        cursorColor = Primary
                    ),
                    isError = pinError != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { confirmPin = it; pinError = null },
                    label = { Text("Thibitisha nenosiri") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        errorBorderColor = Alert,
                        cursorColor = Primary
                    ),
                    isError = pinError != null
                )

                if (pinError != null) {
                    Text(
                        text = pinError!!,
                        color = Alert,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SwitchRow(
                    title = "Tumia kidole gusa / uso",
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Muda wa kufunga kiotomatiki", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("mara_moja" to "Mara moja", "dakika_1" to "Dakika 1", "dakika_5" to "Dakika 5", "kamwe" to "Kamwe")
                    .forEach { (key, label) ->
                        RadioRow(
                            label = label,
                            selected = timeout == key,
                            onSelect = { timeout = key }
                        )
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (lockEnabled && pin.length in 4..6 && pin == confirmPin) {
                        scope.launch {
                            settings.setLockEnabled(true)
                            settings.setLockPinHash(pin)
                            settings.setLockTimeout(timeout)
                            settings.setBiometricEnabled(biometricEnabled)
                        }
                        onBack()
                    } else if (lockEnabled && (pin.length < 4 || pin.length > 6)) {
                        pinError = "Nenosiri lazima liwe tarakimu 4 hadi 6"
                    } else if (lockEnabled && pin != confirmPin) {
                        pinError = "Nambari hazilingani"
                    } else {
                        scope.launch {
                            settings.setLockEnabled(false)
                            settings.setBiometricEnabled(false)
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Hifadhi", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f), fontSize = 15.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun RadioRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Text(label, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
    }
}
