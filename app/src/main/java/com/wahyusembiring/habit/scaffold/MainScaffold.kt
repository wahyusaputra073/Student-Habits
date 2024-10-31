package com.wahyusembiring.habit.scaffold

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wahyusembiring.habit.navigation.MainNavigation
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.model.User
import com.wahyusembiring.habit.MainViewModel
import com.wahyusembiring.habit.util.routeSimpleClassName
import com.wahyusembiring.ui.component.navigationdrawer.DrawerItem
import com.wahyusembiring.ui.component.navigationdrawer.NavigationDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    drawerState: DrawerState,
    user: User? = null,
    screens: NavGraphBuilder.() -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDrawerItem: DrawerItem? = remember(navBackStackEntry) {
        DrawerItem.defaultItems.find {
            it.screen.simpleName == navBackStackEntry.routeSimpleClassName
        }
    }
    val coroutineScope = rememberCoroutineScope()

    NavigationDrawer(
        drawerState = drawerState,
        isGesturesEnabled = selectedDrawerItem != null,
        selectedDrawerItem = selectedDrawerItem ?: DrawerItem.defaultItems.first(),
        imageResourceIdOrUri = user?.photoUrl,
        username = user?.name,
        onDrawerItemClick = {
            onDrawerItemClick(it, drawerState, navController, coroutineScope)
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            MainNavigation(
                navController = navController,
                scaffoldPadding = it,
                builder = screens,
                mainViewModel = mainViewModel
            )
        }
    }
}

private fun onDrawerItemClick(
    drawerItem: DrawerItem,
    drawerState: DrawerState,
    navController: NavHostController,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch { drawerState.close() }
    val screen = when (drawerItem.screen) {
        Screen.Overview::class -> Screen.Overview
        Screen.Calendar::class -> Screen.Calendar
        Screen.ThesisSelection::class -> Screen.ThesisSelection
        Screen.Subject::class -> Screen.Subject
        Screen.Lecture::class -> Screen.Lecture
        Screen.AddLecturer::class -> Screen.AddLecturer
        else -> return
    }
    navController.navigate(screen) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = false
        }
        launchSingleTop = true
    }
}