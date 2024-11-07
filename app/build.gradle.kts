plugins {
    alias(libs.plugins.habit.android.application)
    alias(libs.plugins.habit.android.compose)
    alias(libs.plugins.habit.testing)
    alias(libs.plugins.habit.dagger.hilt)
    alias(libs.plugins.habit.android.room)
    alias(libs.plugins.habit.android.lifecycle)
    alias(libs.plugins.habit.android.navigation)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.wahyusembiring.habit"

    defaultConfig {
        applicationId = "com.wahyusembiring.habit"
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    implementation(libs.androidx.activity.compose)

    implementation(project(":feature:settings"))
    implementation(project(":feature:homework"))
    implementation(project(":feature:subject"))
    implementation(project(":feature:overview"))
    implementation(project(":feature:exam"))
    implementation(project(":feature:reminder"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:lecture"))
    implementation(project(":feature:thesisplanner"))
    implementation(project(":feature:auth"))

//    Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

//   Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.facebook.android.sdk)

}