package com.sendafrica.messeji.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeFormatter {

    fun formatRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Sasa hivi"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
                "Dakika $mins"
            }
            isToday(timestamp) -> {
                val sdf = SimpleDateFormat("HH:mm", Locale("sw"))
                sdf.format(Date(timestamp))
            }
            isYesterday(timestamp) -> "Jana"
            else -> {
                val sdf = SimpleDateFormat("MMM d", Locale("sw"))
                sdf.format(Date(timestamp))
            }
        }
    }

    fun formatMessageTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale("sw"))
        return sdf.format(Date(timestamp))
    }

    fun formatDateHeader(timestamp: Long): String {
        return when {
            isToday(timestamp) -> "Leo"
            isYesterday(timestamp) -> "Jana"
            else -> {
                val sdf = SimpleDateFormat("MMMM d, yyyy", Locale("sw"))
                sdf.format(Date(timestamp))
            }
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
