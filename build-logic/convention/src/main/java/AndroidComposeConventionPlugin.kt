import com.android.build.api.dsl.CommonExtension
import com.wahyusembiring.habit.Config
import com.wahyusembiring.habit.androidCommonExtension
import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {

   override fun apply(target: Project) {
      with(target) {
         with(pluginManager) {
            apply("org.jetbrains.kotlin.plugin.compose")
         }

         androidCommonExtension {
            buildFeatures {
               compose = true
            }
         }

         dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findLibrary("androidx-compose-ui").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            add("implementation", libs.findLibrary("androidx-compose-foundation").get())
            add("implementation", libs.findLibrary("androidx-compose-material3").get())
            add("implementation", libs.findLibrary("androidx-compose-foundation-layout").get())
            add("implementation", libs.findLibrary("androidx-compose-material-iconsExtended").get())
            add("androidTestImplementation", platform(bom))
            add(
               "androidTestImplementation",
               libs.findLibrary("androidx-compose-ui-test-junit4").get()
            )
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
         }
      }
   }
}