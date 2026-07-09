package com.sendafrica.messeji.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.sendafrica.messeji.domain.ContactInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun resolveContact(number: String): ContactInfo? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val cursor = contentResolver.query(
            uri,
            arrayOf(
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_URI
            ),
            null, null, null
        )
        cursor?.use { c ->
            if (c.moveToFirst()) {
                return ContactInfo(
                    id = c.getLong(0),
                    name = c.getString(1),
                    phoneNumber = number,
                    photoUri = c.getString(2)
                )
            }
        }
        return null
    }

    fun searchContacts(query: String): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ? OR " +
                    "${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?",
            arrayOf("%$query%", "%$query%"),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )
        cursor?.use { c ->
            val seen = mutableSetOf<Long>()
            while (c.moveToNext()) {
                val id = c.getLong(0)
                if (id !in seen) {
                    seen.add(id)
                    contacts.add(
                        ContactInfo(
                            id = id,
                            name = c.getString(1) ?: "",
                            phoneNumber = c.getString(2) ?: "",
                            photoUri = c.getString(3)
                        )
                    )
                }
            }
        }
        return contacts
    }

    fun getAllContacts(): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ),
            null, null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )
        cursor?.use { c ->
            val seen = mutableSetOf<Long>()
            while (c.moveToNext()) {
                val id = c.getLong(0)
                if (id !in seen) {
                    seen.add(id)
                    contacts.add(
                        ContactInfo(
                            id = id,
                            name = c.getString(1) ?: "",
                            phoneNumber = c.getString(2) ?: "",
                            photoUri = c.getString(3)
                        )
                    )
                }
            }
        }
        return contacts
    }

    fun isContact(number: String): Boolean {
        return resolveContact(number) != null
    }
}
