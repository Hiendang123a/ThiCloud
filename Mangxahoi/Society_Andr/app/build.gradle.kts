plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.app01"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app01"
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
    packaging  {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/io.netty.versions.properties")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(libs.okhttpLoggingInterceptor)
    implementation(libs.gson)
    implementation(libs.threetenbp)
    implementation(libs.lombok)
    implementation(libs.circleimageview)
    annotationProcessor(libs.lombok)
    implementation("com.google.auth:google-auth-library-oauth2-http:1.22.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0") // Google Drive API
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}