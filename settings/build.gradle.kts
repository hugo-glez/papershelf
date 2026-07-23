plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.papershelf.settings"
    compileSdk = 37

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
