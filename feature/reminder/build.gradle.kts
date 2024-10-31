plugins {
    alias(libs.plugins.habit.android.feature)
}

android {
    namespace = "com.wahyusembiring.reminder"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}