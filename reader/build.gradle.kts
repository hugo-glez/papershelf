plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.papershelf.reader"
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
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.hilt.android)
    implementation(libs.mupdf.viewer)
    implementation(libs.readium.navigator)
    implementation(libs.readium.shared)
    implementation(libs.readium.streamer)
    ksp(libs.hilt.compiler)
}
