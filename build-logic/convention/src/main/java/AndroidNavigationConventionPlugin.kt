import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidNavigationConventionPlugin : Plugin<Project> {

   override fun apply(target: Project) {
      with(target) {
         with(pluginManager) {
            apply("habit.kotlin.serialization")
         }

         dependencies {
            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("hilt-navigation-compose").get())
         }
      }
   }
}