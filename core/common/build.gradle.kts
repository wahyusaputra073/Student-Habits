plugins {
    alias(libs.plugins.habit.android.library)
    alias(libs.plugins.habit.android.compose)
    alias(libs.plugins.habit.kotlin.serialization)
    alias(libs.plugins.habit.testing)
}

android {
    namespace = "com.wahyusembiring.common"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}