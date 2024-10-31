import com.android.build.api.dsl.ApplicationExtension
import com.wahyusembiring.habit.androidApplicationExtension
import com.wahyusembiring.habit.configureAndroidKotlin
import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            androidApplicationExtension {
                configureAndroidKotlin(this)
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("kotlinx-coroutine").get())
            }

        }

    }
}