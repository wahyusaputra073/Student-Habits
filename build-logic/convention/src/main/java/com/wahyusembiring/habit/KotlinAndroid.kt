package com.wahyusembiring.habit

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

internal fun Project.configureAndroidKotlin(
    commonExtention: CommonExtension<*, *, *, *, *, *>
) {
    commonExtention.apply {
        compileSdk = Config.COMPILE_SDK

        defaultConfig {
            if (this is ApplicationDefaultConfig) {
                targetSdk = Config.TARGET_SDK
            }
            minSdk = Config.MIN_SDK
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

        configureKotlinOptions<KotlinAndroidProjectExtension>()

    }
}

private inline fun <reified T : KotlinTopLevelExtension> Project.configureKotlinOptions() =
    configure<T> {
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> error("Unsupported project extension $this ${T::class}")
        }.apply {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }