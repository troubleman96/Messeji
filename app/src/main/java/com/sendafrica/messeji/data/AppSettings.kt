package com.sendafrica.messeji.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "messeji_settings")

data class AppPreferences(
    val themeMode: String = "system",
    val lockEnabled: Boolean = false,
    val lockTimeout: String = "mara_moja",
    val lockPinHash: String = "",
    val biometricEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val notifMtuKwaMtu: Boolean = true,
    val notifPesaOtp: Boolean = true,
    val notifMatangazo: Boolean = false,
    val notifSound: String = "default",
    val notifVibrate: Boolean = true,
    val notifShowLockscreen: Boolean = true,
    val notifShowLockscreenPesaOtp: Boolean = false,
    val defaultSimSlot: Int = 0,
    val autoBackup: Boolean = false,
    val dataSaverMode: String = "wifi_only",
    val onboardingComplete: Boolean = false,
    val backupLastTime: Long = 0L
)

@Singleton
class AppSettings @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LOCK_ENABLED = booleanPreferencesKey("lock_enabled")
        val LOCK_TIMEOUT = stringPreferencesKey("lock_timeout")
        val LOCK_PIN_HASH = stringPreferencesKey("lock_pin_hash")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIF_MTU_KWAMTU = booleanPreferencesKey("notif_mtu_kwa_mtu")
        val NOTIF_PESA_OTP = booleanPreferencesKey("notif_pesa_otp")
        val NOTIF_MATANGAZO = booleanPreferencesKey("notif_matangazo")
        val NOTIF_SOUND = stringPreferencesKey("notif_sound")
        val NOTIF_VIBRATE = booleanPreferencesKey("notif_vibrate")
        val NOTIF_SHOW_LOCKSCREEN = booleanPreferencesKey("notif_show_lockscreen")
        val NOTIF_SHOW_LOCKSCREEN_PESA_OTP = booleanPreferencesKey("notif_show_lockscreen_pesa_otp")
        val DEFAULT_SIM_SLOT = intPreferencesKey("default_sim_slot")
        val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
        val DATA_SAVER_MODE = stringPreferencesKey("data_saver_mode")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val BACKUP_LAST_TIME = longPreferencesKey("backup_last_time")
    }

    val preferences: Flow<AppPreferences> = context.settingsStore.data.map { prefs ->
        AppPreferences(
            themeMode = prefs[Keys.THEME_MODE] ?: "system",
            lockEnabled = prefs[Keys.LOCK_ENABLED] ?: false,
            lockTimeout = prefs[Keys.LOCK_TIMEOUT] ?: "mara_moja",
            lockPinHash = prefs[Keys.LOCK_PIN_HASH] ?: "",
            biometricEnabled = prefs[Keys.BIOMETRIC_ENABLED] ?: false,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            notifMtuKwaMtu = prefs[Keys.NOTIF_MTU_KWAMTU] ?: true,
            notifPesaOtp = prefs[Keys.NOTIF_PESA_OTP] ?: true,
            notifMatangazo = prefs[Keys.NOTIF_MATANGAZO] ?: false,
            notifSound = prefs[Keys.NOTIF_SOUND] ?: "default",
            notifVibrate = prefs[Keys.NOTIF_VIBRATE] ?: true,
            notifShowLockscreen = prefs[Keys.NOTIF_SHOW_LOCKSCREEN] ?: true,
            notifShowLockscreenPesaOtp = prefs[Keys.NOTIF_SHOW_LOCKSCREEN_PESA_OTP] ?: false,
            defaultSimSlot = prefs[Keys.DEFAULT_SIM_SLOT] ?: 0,
            autoBackup = prefs[Keys.AUTO_BACKUP] ?: false,
            dataSaverMode = prefs[Keys.DATA_SAVER_MODE] ?: "wifi_only",
            onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
            backupLastTime = prefs[Keys.BACKUP_LAST_TIME] ?: 0L
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.settingsStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setLockEnabled(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.LOCK_ENABLED] = enabled }
    }

    suspend fun setLockTimeout(timeout: String) {
        context.settingsStore.edit { it[Keys.LOCK_TIMEOUT] = timeout }
    }

    suspend fun setLockPinHash(hash: String) {
        context.settingsStore.edit { it[Keys.LOCK_PIN_HASH] = hash }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setNotifCategory(category: String, enabled: Boolean) {
        context.settingsStore.edit {
            when (category) {
                "mtu_kwa_mtu" -> it[Keys.NOTIF_MTU_KWAMTU] = enabled
                "pesa_otp" -> it[Keys.NOTIF_PESA_OTP] = enabled
                "matangazo" -> it[Keys.NOTIF_MATANGAZO] = enabled
            }
        }
    }

    suspend fun setNotifSound(sound: String) {
        context.settingsStore.edit { it[Keys.NOTIF_SOUND] = sound }
    }

    suspend fun setNotifVibrate(vibrate: Boolean) {
        context.settingsStore.edit { it[Keys.NOTIF_VIBRATE] = vibrate }
    }

    suspend fun setNotifShowLockscreen(show: Boolean) {
        context.settingsStore.edit { it[Keys.NOTIF_SHOW_LOCKSCREEN] = show }
    }

    suspend fun setNotifShowLockscreenPesaOtp(show: Boolean) {
        context.settingsStore.edit { it[Keys.NOTIF_SHOW_LOCKSCREEN_PESA_OTP] = show }
    }

    suspend fun setDefaultSimSlot(slot: Int) {
        context.settingsStore.edit { it[Keys.DEFAULT_SIM_SLOT] = slot }
    }

    suspend fun setAutoBackup(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.AUTO_BACKUP] = enabled }
    }

    suspend fun setDataSaverMode(mode: String) {
        context.settingsStore.edit { it[Keys.DATA_SAVER_MODE] = mode }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.settingsStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setBackupLastTime(time: Long) {
        context.settingsStore.edit { it[Keys.BACKUP_LAST_TIME] = time }
    }
}
