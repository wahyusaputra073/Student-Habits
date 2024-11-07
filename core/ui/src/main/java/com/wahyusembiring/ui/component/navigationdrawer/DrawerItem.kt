package com.wahyusembiring.ui.component.navigationdrawer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.ui.R
import kotlin.reflect.KClass

data class DrawerItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int? = null,
    val screen: KClass<out Screen> = Screen.Overview::class,
    val category: Category
) {
    enum class Category {
        CATEGORY_1, CATEGORY_2, CATEGORY_3
    }

    companion object {
        val defaultItems: List<DrawerItem>
            get() = listOf(
                DrawerItem(
                    title = R.string.home,
                    icon = R.drawable.ic_home,
                    screen = Screen.Overview::class,
                    category = Category.CATEGORY_1
                ),
                DrawerItem(
                    title = R.string.calendar,
                    icon = R.drawable.ic_calendar,
                    screen = Screen.Calendar::class,
                    category = Category.CATEGORY_1
                ),
                DrawerItem(
                    title = R.string.thesisplanner,
                    icon = R.drawable.ic_thesisplanner,
                    screen = Screen.ThesisSelection::class,
                    category = Category.CATEGORY_1
                ),
                DrawerItem(
                    title = R.string.subjects,
                    icon = R.drawable.ic_subjects,
                    screen = Screen.Subject::class,
                    category = Category.CATEGORY_2
                ),
                DrawerItem(
                    title = R.string.lectures,
                    icon = R.drawable.ic_teachers,
                    category = Category.CATEGORY_2,
                    screen = Screen.Lecture::class
                ),
                DrawerItem(
                    title = R.string.information,
                    icon = R.drawable.ic_info,
                    category = Category.CATEGORY_3
                ),
                DrawerItem(
                    title = R.string.settings,
                    icon = R.drawable.ic_settings,
                    category = Category.CATEGORY_3,
                    screen = Screen.Settings::class
                )
            )
    }
}