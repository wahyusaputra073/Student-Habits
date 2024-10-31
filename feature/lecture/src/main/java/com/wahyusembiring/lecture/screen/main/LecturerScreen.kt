package com.wahyusembiring.lecture.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.lecture.R
import com.wahyusembiring.lecture.component.LecturerCard
import com.wahyusembiring.ui.component.topappbar.TopAppBar
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun LecturerScreen(
    viewModel: LecturerScreenViewModel,
    navController: NavController,
    drawerState: DrawerState,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    CollectAsOneTimeEvent(viewModel.navigationEvent) {
        when (it) {
            is LecturerScreenNavigationEvent.NavigateToLecturerDetail -> {
                navController.navigate(Screen.AddLecturer(it.lecturerId))
            }

            is LecturerScreenNavigationEvent.NavigateToAddLecturer -> {
                navController.navigate(Screen.AddLecturer())
            }
        }
    }

    LecturerScreen(
        navController = navController,
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onHamburgerMenuClick = {
            coroutineScope.launch { drawerState.open() }
        }
    )
}

@Composable
private fun LecturerScreen(
    navController: NavController,
    state: LecturerScreenUIState,
    onUIEvent: (LecturerScreenUIEvent) -> Unit,
    onHamburgerMenuClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.lectures),
                onMenuClick = onHamburgerMenuClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onUIEvent(LecturerScreenUIEvent.OnAddLecturerClick(navController))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            items(
                items = state.listOfLecturerWithSubjects,
                key = { it.lecturer.id }
            ) { lectureWithSubjects ->
                LecturerCard(
                    lecturerWithSubjects = lectureWithSubjects,
                    onClick = {
                        onUIEvent(LecturerScreenUIEvent.OnLecturerClick(lectureWithSubjects))
                    },
                    onDeleteClick = {
                        onUIEvent(LecturerScreenUIEvent.OnDeleteLecturerClick(lectureWithSubjects))
                    }
                )
                if (lectureWithSubjects != state.listOfLecturerWithSubjects.last()) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.Large))
                }
            }
        }
    }
}