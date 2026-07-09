package com.sendafrica.messeji.domain

data class ContactInfo(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null
)
