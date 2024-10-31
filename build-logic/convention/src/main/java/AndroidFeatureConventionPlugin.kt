import com.wahyusembiring.habit.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("habit.android.library")
                apply("habit.android.compose")
                apply("habit.dagger.hilt")
                apply("habit.android.navigation")
                apply("habit.testing")
            }

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:data"))
            }
        }
    }
}