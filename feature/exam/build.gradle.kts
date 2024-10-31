plugins {
    alias(libs.plugins.habit.android.feature)
}

android {
    namespace = "com.wahyusembiring.exam"
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}