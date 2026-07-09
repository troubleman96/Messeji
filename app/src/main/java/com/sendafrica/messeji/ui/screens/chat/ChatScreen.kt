package com.sendafrica.messeji.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sendafrica.messeji.ui.theme.Alert
import com.sendafrica.messeji.ui.theme.Primary
import com.sendafrica.messeji.ui.theme.SentMessageBg
import com.sendafrica.messeji.util.TimeFormatter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    threadId: Long,
    address: String,
    contactName: String,
    onBack: () -> Unit,
    onContactDetail: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showSimPicker by remember { mutableStateOf(false) }
    var longPressedMessageId by remember { mutableStateOf<Long?>(null) }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(threadId) {
        viewModel.loadThread(threadId, address, contactName)
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = contactName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (state.selectedSim > 0) {
                            Text(
                                text = "SIM ${state.selectedSim}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Nyuma")
                    }
                },
                actions = {
                    IconButton(onClick = onContactDetail) {
                        Icon(Icons.Default.Phone, contentDescription = "Piga Simu")
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Zaidi")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Zuia") },
                                onClick = { showOverflowMenu = false },
                                leadingIcon = { Icon(Icons.Default.Block, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Nyamazisha") },
                                onClick = { showOverflowMenu = false },
                                leadingIcon = { Icon(Icons.Default.VolumeOff, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Futa Mazungumzo") },
                                onClick = {
                                    showOverflowMenu = false
                                    showDeleteConfirm = true
                                },
                                leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                var lastHeader = ""
                items(state.messages, key = { it.id }) { msg ->
                    val header = TimeFormatter.formatDateHeader(msg.date)
                    if (header != lastHeader) {
                        lastHeader = header
                        Text(
                            text = header,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    MessageBubble(
                        message = msg,
                        onClick = {
                            longPressedMessageId = if (longPressedMessageId == msg.id) null else msg.id
                        }
                    )

                    // Context menu for long-pressed message
                    if (longPressedMessageId == msg.id) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (msg.isSent) 0 else 48.dp, vertical = 2.dp),
                            horizontalArrangement = if (msg.isSent)
                                Arrangement.End else Arrangement.Start
                        ) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(msg.body))
                                    longPressedMessageId = null
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Nakili",
                                    modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { longPressedMessageId = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Forward, contentDescription = "Peleka Mbele",
                                    modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deleteMessage(msg.id)
                                    longPressedMessageId = null
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Futa",
                                    modifier = Modifier.size(16.dp), tint = Alert)
                            }
                            IconButton(
                                onClick = { longPressedMessageId = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = "Taarifa",
                                    modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Sim picker
            if (showSimPicker) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("SIM 1", "SIM 2").forEachIndexed { index, label ->
                        TextButton(
                            onClick = {
                                viewModel.setSimSlot(index + 1)
                                showSimPicker = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                containerColor = if (state.selectedSim == index + 1)
                                    Primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                        ) {
                            Text(label, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Composer
            ComposerBar(
                text = state.inputText,
                onTextChange = { viewModel.updateInput(it) },
                onSend = { viewModel.sendMessage() },
                onSimClick = { showSimPicker = !showSimPicker },
                isSending = state.isSending
            )
        }

        // Delete confirm dialog
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Futa Mazungumzo") },
                text = { Text("Una hakika unataka kufuta mazungumzo haya?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        onBack()
                    }) {
                        Text("Futa", color = Alert)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Ghairi")
                    }
                }
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onClick: () -> Unit
) {
    val alignment = if (message.isSent) Arrangement.End else Arrangement.Start
    val bubbleColor = if (message.isSent) SentMessageBg
    else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isSent) Color.White
    else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(vertical = 2.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isSent) 16.dp else 4.dp,
                bottomEnd = if (message.isSent) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text(
                    text = message.body,
                    color = textColor,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timeFormatted,
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                    if (message.isSent) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                message.status == 0 -> "✓"
                                message.status >= 64 -> "✓✓"
                                else -> "○"
                            },
                            color = textColor.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComposerBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onSimClick: () -> Unit,
    isSending: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            onClick = onSimClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.SimCard,
                contentDescription = "SIM",
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Andika meseji…", fontSize = 15.sp) },
            textStyle = MaterialTheme.typography.bodyMedium,
            maxLines = 5,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() })
        )

        Spacer(modifier = Modifier.width(4.dp))

        if (text.length > 140) {
            Text(
                text = "${text.length}/160",
                fontSize = 11.sp,
                color = if (text.length >= 160) Alert else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        IconButton(
            onClick = onSend,
            enabled = text.isNotBlank() && !isSending,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Tuma",
                tint = if (text.isNotBlank()) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
