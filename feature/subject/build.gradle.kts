plugins {
    alias(libs.plugins.habit.android.feature)
}

android {
    namespace = "com.wahyusembiring.subject"
}

dependencies {
    implementation(project(":datetime"))

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(project(":feature:lecture"))
}