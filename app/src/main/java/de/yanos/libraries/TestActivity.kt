package de.yanos.libraries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.core.utils.NavigationDestination
import de.yanos.libraries.ui.auth.AuthView
import de.yanos.libraries.ui.chat.ChatView

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(activity = this@TestActivity) { modifier, config ->
                val navController = rememberNavController()
                DynamicNavigationScreen(
                    modifier = modifier.padding(top = 48.dp), // TODO: Check statusbar problem
                    config = config,
                    destinations = TEST_DESTINATIONS,
                    navController = navController
                ) { contentModifier ->
                    //NavHost Here
                    TestNavHost(
                        modifier = contentModifier,
                        startRoute = TestRoutes.LOGIN,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun TestNavHost(
    modifier: Modifier = Modifier,
    startRoute: String,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startRoute,
    ) {
        composable(TestRoutes.ONE) {
            Text(text = TestRoutes.ONE)
        }
        composable(TestRoutes.TWO) {
            Text(text = TestRoutes.TWO)
        }
        composable(TestRoutes.THREE) {
            ChatView()
        }
        composable(TestRoutes.FOUR) {
            Text(text = TestRoutes.FOUR)
        }
        composable(TestRoutes.LOGIN) {
            AuthView(modifier = Modifier.verticalScroll(rememberScrollState()), onUserStateChange = {})
        }
    }
}

object TestRoutes {
    internal const val ONE = "Inbox"
    internal const val TWO = "Notes"
    internal const val THREE = "Conversations"
    internal const val FOUR = "Settings"
    internal const val LOGIN = "Login"
}

private val TEST_DESTINATIONS = listOf(
    NavigationDestination.TopDestination(
        route = TestRoutes.ONE,
        selectedIcon = Icons.Default.Inbox,
        unselectedIcon = Icons.Default.Inbox,
        iconTextId = R.string.tab_inbox
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.TWO,
        selectedIcon = Icons.Default.Notes,
        unselectedIcon = Icons.Default.Notes,
        iconTextId = R.string.tab_note
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.THREE,
        selectedIcon = Icons.Default.PhoneInTalk,
        unselectedIcon = Icons.Default.PhoneInTalk,
        iconTextId = R.string.tab_conversation
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.FOUR,
        selectedIcon = Icons.Default.Settings,
        unselectedIcon = Icons.Default.Settings,
        iconTextId = R.string.tab_settings
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.LOGIN,
        selectedIcon = Icons.Default.Login,
        unselectedIcon = Icons.Default.Login,
        iconTextId = R.string.tab_login
    ),
)