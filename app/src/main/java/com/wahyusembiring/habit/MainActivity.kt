package com.wahyusembiring.habit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wahyusembiring.habit.navigation.addLectureScreen
import com.wahyusembiring.habit.navigation.blankScreen
import com.wahyusembiring.habit.navigation.calendarScreen
import com.wahyusembiring.habit.navigation.createHomeworkScreen
import com.wahyusembiring.habit.navigation.createReminderScreen
import com.wahyusembiring.habit.navigation.createSubjectScreen
import com.wahyusembiring.habit.navigation.examScreen
import com.wahyusembiring.habit.navigation.subjectScreen
import com.wahyusembiring.habit.navigation.lectureScreen
import com.wahyusembiring.habit.navigation.loginScreen
import com.wahyusembiring.habit.navigation.onBoardingScreen
import com.wahyusembiring.habit.navigation.overviewScreen
import com.wahyusembiring.habit.navigation.thesisPlannerScreen
import com.wahyusembiring.habit.navigation.thesisSelectionScreen
import com.wahyusembiring.habit.scaffold.MainScaffold
import com.wahyusembiring.ui.theme.HabitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels<MainViewModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        auth = Firebase.auth
        splashScreen.setKeepOnScreenCondition { !viewModel.isAppReady.value }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val user by viewModel.currentUser.collectAsStateWithLifecycle(null)

                MainScaffold(
                    mainViewModel = viewModel,
                    navController = navController,
                    drawerState = drawerState,
                    user = user,
                    screens = {
                        blankScreen()
                        createHomeworkScreen(navController)
                        overviewScreen(navController, drawerState)
                        createSubjectScreen(navController)
                        examScreen(navController)
                        createReminderScreen(navController)
                        calendarScreen(navController, drawerState)
                        onBoardingScreen(navController)
                        thesisSelectionScreen(navController, drawerState)
                        thesisPlannerScreen(navController, drawerState)
                        subjectScreen(navController, drawerState)
                        lectureScreen(navController, drawerState)
                        addLectureScreen(navController)
                        loginScreen(navController)
                    }
                )
            }
        }
    }

    override fun onPause() {
        viewModel.onActivityPause()
        super.onPause()
    }

}