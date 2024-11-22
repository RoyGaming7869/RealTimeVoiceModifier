plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.real.time.voice.modifier"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.real.time.voice.modifier"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.github.bullheadandplato:AndroidEqualizer:2.2")
    implementation("io.github.gautamchibde:audiovisualizer:2.2.5")
    implementation ("com.github.zhpanvip:viewpagerindicator:1.2.3")
    implementation ("com.google.android.play:feature-delivery-ktx:2.1.0")
    implementation ("com.airbnb.android:lottie:6.3.0")
    implementation("com.google.firebase:firebase-crashlytics:19.2.1")

}