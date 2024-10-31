plugins {
    alias(libs.plugins.habit.android.library)
    alias(libs.plugins.habit.android.compose)
    alias(libs.plugins.habit.testing)
}

android {
    namespace = "com.wahyusembiring.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":datetime"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.text.google.fonts)

    //   Coil image loader
    implementation(libs.coil.compose)

    //   Compose Color Picker
    implementation(libs.compose.colorpicker)
}