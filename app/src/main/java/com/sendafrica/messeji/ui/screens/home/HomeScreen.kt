package com.sendafrica.messeji.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sendafrica.messeji.domain.ThreadInfo
import com.sendafrica.messeji.ui.theme.Accent
import com.sendafrica.messeji.ui.theme.Alert
import com.sendafrica.messeji.ui.theme.Primary
import com.sendafrica.messeji.util.TimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onThreadClick: (Long, String, String) -> Unit,
    onNewMessage: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onContacts: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filteredThreads = viewModel.getFilteredThreads()
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Messeji",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    if (state.isMultiSelectMode) {
                        IconButton(onClick = { viewModel.exitMultiSelectMode() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Toka")
                        }
                    } else {
                        IconButton(onClick = onSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Tafuta")
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
                                    text = { Text("Chagua") },
                                    onClick = {
                                        showOverflowMenu = false
                                        viewModel.enterMultiSelectMode()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Hifadhi Zote") },
                                    onClick = { showOverflowMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Mipangilio") },
                                    onClick = {
                                        showOverflowMenu = false
                                        onSettings()
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!state.isMultiSelectMode) {
                FloatingActionButton(
                    onClick = onNewMessage,
                    containerColor = Primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Meseji Mpya")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            // Multi-select action bar
            if (state.isMultiSelectMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { viewModel.archiveThread(state.selectedThreadIds.firstOrNull() ?: return@TextButton) }) {
                        Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hifadhi", fontSize = 13.sp)
                    }
                    TextButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Alert)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Futa", fontSize = 13.sp, color = Alert)
                    }
                }
            }

            // Content
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Inapakia…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                filteredThreads.isEmpty() -> {
                    EmptyState(filter = state.selectedFilter, onNewMessage = onNewMessage)
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredThreads, key = { it.threadId }) { thread ->
                            ThreadItem(
                                thread = thread,
                                isSelected = thread.threadId in state.selectedThreadIds,
                                isMultiSelect = state.isMultiSelectMode,
                                onClick = {
                                    if (state.isMultiSelectMode) {
                                        viewModel.toggleThreadSelection(thread.threadId)
                                    } else {
                                        viewModel.loadThreads()
                                        onThreadClick(thread.threadId, thread.address, thread.contactName)
                                    }
                                },
                                onLongClick = {
                                    if (!state.isMultiSelectMode) {
                                        viewModel.enterMultiSelectMode()
                                        viewModel.toggleThreadSelection(thread.threadId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Futa Mazungumzo") },
                text = { Text("Una hakika unataka kufuta mazungumzo yaliyochaguliwa?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteSelectedThreads()
                        showDeleteConfirm = false
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
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "zote" to "Zote",
        "mtu_kwa_mtu" to "Mtu kwa Mtu",
        "pesa_na_otp" to "Pesa na OTP",
        "matangazo" to "Matangazo"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (key, label) ->
            FilterChip(
                selected = selectedFilter == key,
                onClick = { onFilterSelected(key) },
                label = { Text(label, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ThreadItem(
    thread: ThreadInfo,
    isSelected: Boolean,
    isMultiSelect: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 4.dp, vertical = 1.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = thread.contactName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (thread.isPinned) {
                            Icon(
                                Icons.Default.Pin,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                        Text(
                            text = thread.contactName,
                            fontWeight = if (!thread.read) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (thread.isMuted) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.VolumeOff,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = TimeFormatter.formatRelative(thread.date),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = thread.snippet ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (!thread.read) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Accent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(filter: String, onNewMessage: () -> Unit) {
    val message = when (filter) {
        "mtu_kwa_mtu" -> "Hakuna meseji za Mtu kwa Mtu."
        "pesa_na_otp" -> "Hakuna meseji za Pesa na OTP."
        "matangazo" -> "Hakuna matangazo."
        else -> "Hakuna mazungumzo bado. Anza kwa kutuma meseji yako ya kwanza!"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
