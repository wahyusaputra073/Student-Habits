import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
   `kotlin-dsl`
}

group = "com.wahyusembiring.habit.buildlogic"

java {
   sourceCompatibility = JavaVersion.VERSION_17
   targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
   compilerOptions {
      jvmTarget = JvmTarget.JVM_17
   }
}

dependencies {
   compileOnly(libs.android.gradlePlugin)
   compileOnly(libs.android.tools.common)
   compileOnly(libs.compose.gradlePlugin)
   compileOnly(libs.kotlin.gradlePlugin)
   compileOnly(libs.ksp.gradlePlugin)
   compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
   plugins {
      register("androidApplication") {
         id = "habit.android.application"
         implementationClass = "AndroidApplicationConventionPlugin"
      }
      register("androidCompose") {
         id = "habit.android.compose"
         implementationClass = "AndroidComposeConventionPlugin"
      }
      register("androidLibrary") {
         id = "habit.android.library"
         implementationClass = "AndroidLibraryConventionPlugin"
      }
      register("daggerHilt") {
         id = "habit.dagger.hilt"
         implementationClass = "DaggerHiltConventionPlugin"
      }
      register("androidRoom") {
         id = "habit.android.room"
         implementationClass = "AndroidRoomConventionPlugin"
      }
      register("testing") {
         id = "habit.testing"
         implementationClass = "TestingConventionPlugin"
      }
      register("androidLifecycle") {
         id = "habit.android.lifecycle"
         implementationClass = "AndroidLifecycleConventionPlugin"
      }
      register("kotlinSerialization") {
         id = "habit.kotlin.serialization"
         implementationClass = "KotlinSerializationConventionPlugin"
      }
      register("androidNavigation") {
         id = "habit.android.navigation"
         implementationClass = "AndroidNavigationConventionPlugin"
      }
      register("androidFeature") {
         id = "habit.android.feature"
         implementationClass = "AndroidFeatureConventionPlugin"
      }
   }
}