package com.wahyusembiring.thesisplanner.screen.thesisselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.imageLoader
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.thesisplanner.R
import com.wahyusembiring.thesisplanner.component.Section
import com.wahyusembiring.thesisplanner.component.ThesisList
import com.wahyusembiring.ui.component.topappbar.TopAppBar
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun ThesisSelectionScreen(
    viewModel: ThesisSelectionScreenViewModel,
    drawerState: DrawerState,
    navController: NavHostController
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    ThesisSelectionScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onNavigateToThesisPlanner = {
            navController.navigate(Screen.ThesisPlanner(it))
        },
        onHamburgerMenuClick = {
            coroutineScope.launch { drawerState.open() }
        }
    )
}

@Composable
private fun ThesisSelectionScreen(
    state: ThesisSelectionScreenUIState,
    onUIEvent: (ThesisSelectionScreenUIEvent) -> Unit,
    onNavigateToThesisPlanner: (thesisId: Int) -> Unit,
    onHamburgerMenuClick: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.thesis_planner),
                onMenuClick = onHamburgerMenuClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onUIEvent(
                        ThesisSelectionScreenUIEvent.OnCreateNewThesisClick(
                            onNavigateToThesisPlanner = onNavigateToThesisPlanner
                        )
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = stringResource(R.string.create_new_thesis)
                )
            }
        }
    ) { scaffoldPadding ->
        if (state.listOfThesis.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(scaffoldPadding)
            ) {
                Section(
                    title = stringResource(R.string.thesis_list)
                ) {
                    ThesisList(
                        listOfThesis = state.listOfThesis,
                        onThesisClick = {
                            onNavigateToThesisPlanner(it.thesis.id)
                        },
                        onDeleteThesis = {
                            onUIEvent(ThesisSelectionScreenUIEvent.OnDeleteThesisClick(it))
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .width(100.dp),
                        model = R.drawable.no_data_picture,
                        contentDescription = stringResource(R.string.you_dont_have_any_thesis),
                        imageLoader = context.imageLoader
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                    Text(
                        text = stringResource(R.string.you_dont_have_any_thesis),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}