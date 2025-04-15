package com.reyaz.chatsupport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reyaz.chatsupport.di.allModules
import com.reyaz.chatsupport.navigation.ChatListNavigation
import com.reyaz.chatsupport.navigation.UserChatNavigation
import com.reyaz.chatsupport.system.NetworkMonitor
import com.reyaz.chatsupport.ui.screens.chatlist.ChatListScreen
import com.reyaz.chatsupport.ui.screens.userchat.UserChatScreen
import com.reyaz.chatsupport.ui.theme.ChatSupportTheme
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatSupportTheme {
                MainScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val networkMonitor = getKoin().get<NetworkMonitor>()
    DisposableEffect(Unit) {
        networkMonitor.start()
        onDispose {
            networkMonitor.stop()
        }
    }

    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ChatListNavigation,
    ) {
        composable<ChatListNavigation> {
            ChatListScreen { navController.navigate(UserChatNavigation(it)) }
        }
        composable<UserChatNavigation> {
            UserChatScreen()
        }
    }
}