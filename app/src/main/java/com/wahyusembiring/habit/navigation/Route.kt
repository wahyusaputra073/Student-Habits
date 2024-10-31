package com.wahyusembiring.habit.navigation

import androidx.compose.material3.DrawerState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.wahyusembiring.auth.login.LoginScreen
import com.wahyusembiring.auth.login.LoginScreenViewModel
import com.wahyusembiring.calendar.CalendarScreen
import com.wahyusembiring.calendar.CalendarScreenViewModel
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.exam.ExamScreen
import com.wahyusembiring.exam.ExamScreenViewModel
import com.wahyusembiring.subject.screen.main.SubjectScreen
import com.wahyusembiring.subject.screen.main.SubjectScreenViewModel
import com.wahyusembiring.homework.CreateHomeworkScreen
import com.wahyusembiring.overview.OverviewScreen
import com.wahyusembiring.homework.CreateHomeworkScreenViewModel
import com.wahyusembiring.lecture.screen.addlecture.AddLectureScreen
import com.wahyusembiring.lecture.screen.addlecture.AddLecturerScreenViewModel
import com.wahyusembiring.lecture.screen.main.LecturerScreen
import com.wahyusembiring.lecture.screen.main.LecturerScreenViewModel
import com.wahyusembiring.onboarding.OnBoardingScreen
import com.wahyusembiring.onboarding.OnBoardingScreenViewModel
import com.wahyusembiring.overview.OverviewViewModel
import com.wahyusembiring.reminder.CreateReminderScreen
import com.wahyusembiring.reminder.CreateReminderScreenViewModel
import com.wahyusembiring.subject.screen.create.CreateSubjectScreen
import com.wahyusembiring.subject.screen.create.CreateSubjectViewModel
import com.wahyusembiring.thesisplanner.screen.planner.ThesisPlannerScreen
import com.wahyusembiring.thesisplanner.screen.planner.ThesisPlannerScreenViewModel
import com.wahyusembiring.thesisplanner.screen.thesisselection.ThesisSelectionScreen
import com.wahyusembiring.thesisplanner.screen.thesisselection.ThesisSelectionScreenViewModel

fun NavGraphBuilder.blankScreen() = composable<Screen.Blank>(content = {})

fun NavGraphBuilder.createHomeworkScreen(
    navController: NavHostController
) {
    composable<Screen.CreateHomework> {
        val route = it.toRoute<Screen.CreateHomework>()

        val viewModel: CreateHomeworkScreenViewModel = hiltViewModel(
            viewModelStoreOwner = it,
            creationCallback = { factory: CreateHomeworkScreenViewModel.Factory ->
                factory.create(route.homeworkId)
            }
        )
        CreateHomeworkScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.overviewScreen(
    navController: NavHostController,
    drawerState: DrawerState
) {
    composable<Screen.Overview> {
        val overviewViewModel: OverviewViewModel = hiltViewModel(it)
        OverviewScreen(
            viewModel = overviewViewModel,
            drawerState = drawerState,
            navController = navController
        )
    }
}

fun NavGraphBuilder.createSubjectScreen(
    navController: NavHostController
) {
    composable<Screen.CreateSubject> {
        val viewModel: CreateSubjectViewModel = hiltViewModel(it)
        CreateSubjectScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.examScreen(
    navController: NavHostController
) {
    composable<Screen.CreateExam> {
        val route = it.toRoute<Screen.CreateExam>()
        val viewModel: ExamScreenViewModel = hiltViewModel(
            viewModelStoreOwner = it,
            creationCallback = { factory: ExamScreenViewModel.Factory ->
                factory.create(route.examId)
            }
        )
        ExamScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.createReminderScreen(
    navController: NavHostController
) {
    composable<Screen.CreateReminder> {
        val route = it.toRoute<Screen.CreateReminder>()
        val viewModel: CreateReminderScreenViewModel = hiltViewModel(
            viewModelStoreOwner = it,
            creationCallback = { factory: CreateReminderScreenViewModel.Factory ->
                factory.create(route.reminderId)
            }
        )
        CreateReminderScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.calendarScreen(
    navController: NavHostController,
    drawerState: DrawerState
) {
    composable<Screen.Calendar> {
        val viewModel: CalendarScreenViewModel = hiltViewModel(it)
        CalendarScreen(
            viewModel = viewModel,
            navController = navController,
            drawerState = drawerState
        )
    }
}

fun NavGraphBuilder.onBoardingScreen(
    navController: NavHostController
) {
    composable<Screen.OnBoarding> {
        val viewModel: OnBoardingScreenViewModel = hiltViewModel(it)
        OnBoardingScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.thesisSelectionScreen(
    navController: NavHostController,
    drawerState: DrawerState
) {
    composable<Screen.ThesisSelection> {
        val viewModel: ThesisSelectionScreenViewModel = hiltViewModel(it)
        ThesisSelectionScreen(
            viewModel = viewModel,
            drawerState = drawerState,
            navController = navController
        )
    }
}


fun NavGraphBuilder.thesisPlannerScreen(
    navController: NavHostController,
    drawerState: DrawerState
) {
    composable<Screen.ThesisPlanner> {
        val thesisId = it.toRoute<Screen.ThesisPlanner>().thesisId

        val viewModel: ThesisPlannerScreenViewModel = hiltViewModel(
            viewModelStoreOwner = it,
            creationCallback = { factory: ThesisPlannerScreenViewModel.Factory ->
                factory.create(thesisId)
            }
        )
        ThesisPlannerScreen(
            viewModel = viewModel,
            drawerState = drawerState,
            navController = navController
        )
    }
}

fun NavGraphBuilder.subjectScreen(
    navController: NavHostController,
    drawerState: DrawerState
) {
    composable<Screen.Subject> {
        val viewModel: SubjectScreenViewModel = hiltViewModel(it)
        SubjectScreen(
            viewModel = viewModel,
            navController = navController,
            drawerState = drawerState
        )
    }
}

fun NavGraphBuilder.lectureScreen(
    navController: NavHostController,
    drawerState: DrawerState
) {
    composable<Screen.Lecture> {
        val viewModel: LecturerScreenViewModel = hiltViewModel()
        LecturerScreen(
            viewModel = viewModel,
            navController = navController,
            drawerState = drawerState
        )
    }
}

fun NavGraphBuilder.addLectureScreen(
    navController: NavHostController
) {
    composable<Screen.AddLecturer> {
        val lectureId = it.toRoute<Screen.AddLecturer>().lecturerId
        val viewModel: AddLecturerScreenViewModel = hiltViewModel(
            viewModelStoreOwner = it,
            creationCallback = { factory: AddLecturerScreenViewModel.Factory ->
                factory.create(lectureId)
            }
        )
        AddLectureScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.loginScreen(
    navController: NavHostController
) {
    composable<Screen.Login> {
        val viewModel: LoginScreenViewModel = hiltViewModel()
        LoginScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}