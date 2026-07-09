package com.sendafrica.messeji.domain

data class ThreadInfo(
    val threadId: Long,
    val address: String,
    val contactName: String,
    val contactPhotoUri: String? = null,
    val snippet: String? = null,
    val date: Long,
    val messageCount: Int,
    val read: Boolean,
    val category: String = "person",
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val hasAttachment: Boolean = false
)
