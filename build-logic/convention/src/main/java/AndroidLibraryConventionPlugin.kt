import com.android.build.api.dsl.LibraryExtension
import com.wahyusembiring.habit.androidLibraryExtension
import com.wahyusembiring.habit.configureAndroidKotlin
import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {

   override fun apply(target: Project) {
      with(target) {
         with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
         }

         androidLibraryExtension {
            configureAndroidKotlin(this)
            defaultConfig {
               consumerProguardFiles("consumer-rules.pro")
            }
         }

         dependencies {
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("implementation", libs.findLibrary("kotlinx-coroutine").get())
         }

      }
   }
}