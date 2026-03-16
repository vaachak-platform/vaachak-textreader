
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // <-- Remove 'version "2.0.21"' from here

    // IMPORTANT: Because you are using Kotlin 2.0+, you must add the new Compose compiler plugin here:
    id("org.jetbrains.kotlin.plugin.compose")
}
val releaseKeystoreFile = file("vaachak-key.jks")
val releaseStorePassword =
    System.getenv("ORG_GRADLE_PROJECT_VAACHAK_KEYSTORE_PASSWORD")
        ?: providers.gradleProperty("VAACHAK_KEYSTORE_PASSWORD").orNull
val releaseKeyAlias =
    System.getenv("ORG_GRADLE_PROJECT_VAACHAK_KEY_ALIAS")
        ?: providers.gradleProperty("VAACHAK_KEY_ALIAS").orNull
val releaseKeyPassword =
    System.getenv("ORG_GRADLE_PROJECT_VAACHAK_KEY_PASSWORD")
        ?: providers.gradleProperty("VAACHAK_KEY_PASSWORD").orNull
android {
    namespace = "org.vaachak.textreader"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.vaachak.textreader"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    applicationVariants.all {
        val variantName = this.buildType.name // "debug" or "release"

        outputs.all {
            val outputImpl = this as? BaseVariantOutputImpl

            // This will name it exactly Vaachak-TextReader.apk
            outputImpl?.outputFileName = "Vaachak-TextReader.apk"

            // PRO TIP: If you want to tell your local builds apart,
            // you can uncomment the line below instead to get
            // Vaachak-TextReader-debug.apk and Vaachak-TextReader-release.apk
            outputImpl?.outputFileName = "Vaachak-TextReader-${variantName}.apk"
        }
    }
    signingConfigs.create("release") {


        storeFile = releaseKeystoreFile
        storePassword =  releaseStorePassword
        keyAlias =  releaseKeyAlias
        keyPassword =  releaseKeyPassword
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI & Material 3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Unit Testing (Pure JVM)
    testImplementation(libs.junit)

    // Android UI Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debugging tools
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
