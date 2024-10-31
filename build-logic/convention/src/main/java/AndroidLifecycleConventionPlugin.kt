import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLifecycleConventionPlugin : Plugin<Project> {

   override fun apply(target: Project) {
      with(target) {
         dependencies {
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-ktx").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-savedstate").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
            add("ksp", libs.findLibrary("androidx-lifecycle-compiler").get())
         }
      }
   }
}