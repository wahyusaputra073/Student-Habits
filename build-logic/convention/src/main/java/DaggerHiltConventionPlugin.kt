import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DaggerHiltConventionPlugin : Plugin<Project> {

   override fun apply(target: Project) {
      with(target) {
         with(pluginManager) {
            apply("com.google.devtools.ksp")
            apply("com.google.dagger.hilt.android")
         }

         dependencies {
            add("implementation", libs.findLibrary("hilt.android").get())
//            add("implementation", libs.findLibrary("javax-inject").get())
            add("ksp", libs.findLibrary("hilt.android.compiler").get())
         }
      }
   }
}