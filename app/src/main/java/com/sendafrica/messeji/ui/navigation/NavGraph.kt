package com.sendafrica.messeji.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sendafrica.messeji.ui.screens.about.AboutScreen
import com.sendafrica.messeji.ui.screens.backup.BackupRestoreScreen
import com.sendafrica.messeji.ui.screens.blocked.BlockedNumbersScreen
import com.sendafrica.messeji.ui.screens.chat.ChatScreen
import com.sendafrica.messeji.ui.screens.contacts.ContactDetailScreen
import com.sendafrica.messeji.ui.screens.contacts.ContactsScreen
import com.sendafrica.messeji.ui.screens.home.HomeScreen
import com.sendafrica.messeji.ui.screens.lock.LockSetupScreen
import com.sendafrica.messeji.ui.screens.lock.LockScreen
import com.sendafrica.messeji.ui.screens.newmessage.NewMessageScreen
import com.sendafrica.messeji.ui.screens.onboarding.OnboardingScreen
import com.sendafrica.messeji.ui.screens.search.SearchScreen
import com.sendafrica.messeji.ui.screens.settings.SettingsScreen
import com.sendafrica.messeji.ui.screens.settings.SettingsSubScreens

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Chat : Screen("chat/{threadId}/{address}/{name}") {
        fun createRoute(threadId: Long, address: String, name: String): String {
            return "chat/$threadId/$address/$name"
        }
    }
    data object NewMessage : Screen("new_message")
    data object Contacts : Screen("contacts")
    data object ContactDetail : Screen("contact_detail/{contactId}/{name}/{phone}") {
        fun createRoute(contactId: Long, name: String, phone: String): String {
            return "contact_detail/$contactId/$name/$phone"
        }
    }
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object SettingsNotifications : Screen("settings/notifications")
    data object SettingsAppearance : Screen("settings/appearance")
    data object SettingsSim : Screen("settings/sim")
    data object SettingsBlocked : Screen("settings/blocked")
    data object SettingsBackup : Screen("settings/backup")
    data object SettingsLock : Screen("settings/lock")
    data object SettingsData : Screen("settings/data")
    data object SettingsAbout : Screen("settings/about")
    data object SettingsHelp : Screen("settings/help")
    data object LockSetup : Screen("lock_setup")
    data object Lock : Screen("lock")
    data object About : Screen("about")
    data object BackupRestore : Screen("backup_restore")
    data object BlockedNumbers : Screen("blocked_numbers")
    data object LockScreen : Screen("lock_screen")
}

@Composable
fun MessejiNavGraph(
    navController: NavHostController,
    startDestination: String,
    isLocked: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLocked) Screen.Lock.route else startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onThreadClick = { threadId, address, name ->
                    navController.navigate(Screen.Chat.createRoute(threadId, address, name))
                },
                onNewMessage = { navController.navigate(Screen.NewMessage.route) },
                onSearch = { navController.navigate(Screen.Search.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onContacts = { navController.navigate(Screen.Contacts.route) }
            )
        }

        composable(
            Screen.Chat.route,
            arguments = listOf(
                navArgument("threadId") { type = NavType.LongType },
                navArgument("address") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val threadId = backStackEntry.arguments?.getLong("threadId") ?: return@composable
            val address = backStackEntry.arguments?.getString("address") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ChatScreen(
                threadId = threadId,
                address = address,
                contactName = name,
                onBack = { navController.popBackStack() },
                onContactDetail = {
                    navController.navigate(Screen.ContactDetail.createRoute(0, name, address))
                }
            )
        }

        composable(Screen.NewMessage.route) {
            NewMessageScreen(
                onBack = { navController.popBackStack() },
                onSend = { threadId, address, name ->
                    navController.navigate(Screen.Chat.createRoute(threadId, address, name)) {
                        popUpTo(Screen.NewMessage.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Contacts.route) {
            ContactsScreen(
                onBack = { navController.popBackStack() },
                onContactClick = { id, name, phone ->
                    navController.navigate(Screen.ContactDetail.createRoute(id, name, phone))
                }
            )
        }

        composable(
            Screen.ContactDetail.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType },
                navArgument("name") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: 0
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            ContactDetailScreen(
                contactId = contactId,
                name = name,
                phone = phone,
                onBack = { navController.popBackStack() },
                onSendMessage = { address ->
                    navController.navigate(Screen.Chat.createRoute(0, address, name))
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onMessageClick = { threadId, address, name ->
                    navController.navigate(Screen.Chat.createRoute(threadId, address, name))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { subScreen ->
                    navController.navigate(subScreen.route)
                }
            )
        }

        composable(Screen.SettingsNotifications.route) {
            SettingsSubScreens.NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SettingsAppearance.route) {
            SettingsSubScreens.AppearanceScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SettingsBlocked.route) {
            BlockedNumbersScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SettingsBackup.route) {
            BackupRestoreScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SettingsLock.route) {
            LockSetupScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SettingsAbout.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Lock.route) {
            LockScreen(onUnlock = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Lock.route) { inclusive = true }
                }
            })
        }
    }
}
