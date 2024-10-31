plugins {
    alias(libs.plugins.habit.android.feature)
}

android {
    namespace = "com.wahyusembiring.calendar"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

}

dependencies {

    //   boguszpawlowski compose calendar
    implementation(libs.boguszpawlowski.compose.calendar)
    implementation(libs.boguszpawlowski.kotlix.datetime)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

}