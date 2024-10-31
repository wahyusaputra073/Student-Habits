plugins {
    alias(libs.plugins.habit.android.feature)
}

android {
    namespace = "com.wahyusembiring.auth"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.facebook.android.sdk)
}