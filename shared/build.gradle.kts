import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.kotlinSerialization)

}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Shared module for AuriApplication"
        homepage = "https://github.com/example/AuriApplication"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "Shared"
            isStatic = true
        }
        pod("GoogleMaps") {
            version = "9.2.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    android {
       namespace = "com.example.auriapplication.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.core.ktx)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kstoreFile)
            implementation("com.google.maps.android:maps-compose:8.3.0")
            implementation("com.google.android.gms:play-services-maps:20.0.0")

            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.geocoder.mobile)
            implementation(libs.compass.autocomplete.mobile)
            implementation(libs.compass.permissions.mobile)
        }
        jvmMain.dependencies {
            implementation(libs.kstoreFile)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIcons)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kstore)
            implementation(libs.kotlinx.io)
            implementation(libs.okio)
            implementation(libs.kotlinx.datetime)

            //ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.navigator)
            implementation(libs.navigator.tab)
            implementation(libs.navigator.transitions)
            implementation(libs.kamel.image)

            // Geolocation
            implementation(libs.compass.geolocation)
            // Geocoding
            implementation(libs.compass.geocoder)
            // Autocomplete
            implementation(libs.compass.autocomplete)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.compass.geolocation.mobile)
            implementation(libs.compass.geocoder.mobile)
            implementation(libs.compass.autocomplete.mobile)
            implementation(libs.compass.permissions.mobile)
            implementation(libs.ktor.client.darwin)
            implementation(libs.kstoreFile)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.compass.geolocation.browser)
            implementation(libs.compass.geocoder.web)
            implementation(libs.compass.autocomplete.web)
            implementation(libs.kstoreFile)
        }
        wasmJsMain.dependencies {
            implementation(libs.compass.geolocation.browser)
            implementation(libs.compass.geocoder.web)
            implementation(libs.compass.autocomplete.web)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
