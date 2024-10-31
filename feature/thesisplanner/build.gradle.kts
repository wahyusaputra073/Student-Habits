plugins {
    alias(libs.plugins.habit.android.feature)
}

android {
    namespace = "com.wahyusembiring.thesisplanner"
}

dependencies {
    implementation(project(":datetime"))

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
}