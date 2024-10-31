package com.wahyusembiring.habit

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
   get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.androidCommonExtension(
   configurationBlock: CommonExtension<*, *, *, *, *, *>.() -> Unit
) {
   val androidExtension = extensions.getByName("android") as CommonExtension<*, *, *, *, *, *>
   androidExtension.apply {
      configurationBlock()
   }
}

fun Project.androidApplicationExtension(
   configurationBlock: ApplicationExtension.() -> Unit
) {
   val androidExtension = extensions.getByType<ApplicationExtension>()
   androidExtension.apply {
      configurationBlock()
   }
}

fun Project.androidLibraryExtension(
   configurationBlock: LibraryExtension.() -> Unit
) {
   val androidExtension = extensions.getByType<LibraryExtension>()
   androidExtension.apply {
      configurationBlock()
   }
}