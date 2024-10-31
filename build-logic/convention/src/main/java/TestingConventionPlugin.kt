import com.wahyusembiring.habit.androidCommonExtension
import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class TestingConventionPlugin : Plugin<Project> {

   override fun apply(target: Project) {
      with(target) {

         androidCommonExtension {
            defaultConfig {
               testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
         }

         dependencies {
            add("testImplementation", kotlin("test"))
            add("testImplementation", libs.findLibrary("junit").get())
            add("androidTestImplementation", kotlin("test"))
            add("androidTestImplementation", libs.findLibrary("androidx-test-junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx-test-espresso-core").get())
         }
      }
   }
}