plugins {
    alias(libs.plugins.habit.android.library)
    alias(libs.plugins.habit.testing)
}

android {
    namespace = "com.wahyusembiring.datetime"
}

dependencies {
    // Kotlinx datetime
    implementation(libs.kotlinx.datetime)
}