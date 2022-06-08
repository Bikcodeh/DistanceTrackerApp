import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    id(Modules.Plugins.androidApplication)
    id(Modules.Plugins.kotlinAndroid)
    id(Modules.Plugins.mapsPlatform)
    id(Modules.Plugins.navigationArgs)
    id(Modules.Plugins.kotlinParcelize)
    id(Modules.Plugins.kotlinKapt)
    id(Modules.Plugins.daggerHilt)
}
android {
    compileSdk = Modules.AndroidSdk.compile

    defaultConfig {
        applicationId = "com.bikcodeh.distancetrackerapp"
        minSdk = Modules.AndroidSdk.minSdk
        targetSdk = Modules.AndroidSdk.target
        versionCode = Modules.AndroidSdk.versionCode
        versionName = Modules.AndroidSdk.versionName

        testInstrumentationRunner = Modules.AndroidSdk.instrumentationRunner
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {

        val properties = Properties()
        properties.load(FileInputStream(rootProject.file("config.properties")))

        getByName("debug") {
            val apiKeyDebug = properties.getProperty("MAPS_API_KEY_DEBUG")
            val appCenterKey = properties.getProperty("APP_CENTER_KEY")
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField(
                type = "String",
                name = "MAPS_API_KEY",
                value =  '"'+apiKeyDebug+'"'
            )
            buildConfigField(
                type = "String",
                name = "APP_CENTER_KEY",
                value = '"'+appCenterKey+'"'
            )
            manifestPlaceholders.putAll(mapOf(
                "API_KEY" to apiKeyDebug
            ))
        }

        getByName("release") {
            val apiKeyRelease = properties.getProperty("MAPS_API_KEY")
            val appCenterKey = properties.getProperty("APP_CENTER_KEY")
            versionNameSuffix = "-full"
            applicationIdSuffix = ".prod"

            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                type = "String",
                name = "MAPS_API_KEY",
                value = '"'+apiKeyRelease+'"'
            )
            buildConfigField(
                type = "String",
                name = "APP_CENTER_KEY",
                value = '"'+appCenterKey+'"'
            )
            manifestPlaceholders.putAll(mapOf(
                "API_KEY" to apiKeyRelease
            ))
        }

        applicationVariants.all {
            val variant = this
            variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val outputFileName = "distance-tracker-${variant.versionName}-${variant.baseName}.apk"
                    output.outputFileName = outputFileName
                }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        android.buildFeatures.dataBinding = true
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(Modules.Libraries.coreKtxLib)
    implementation(Modules.Libraries.appCompatLib)
    implementation(Modules.Libraries.materialLib)
    implementation(Modules.Libraries.mapsLib)
    implementation(Modules.Libraries.constraintLayoutLib)
    implementation(Modules.Libraries.lifecycleRuntimeLib)
    implementation(Modules.Libraries.coroutinesAndroidLib)
    implementation(Modules.Libraries.coroutinesCoreLib)
    implementation(Modules.Libraries.appCenterAnalyticsLib)
    implementation(Modules.Libraries.appCenterCrashesLib)
    implementation(Modules.Libraries.navigationFragmentLib)
    implementation(Modules.Libraries.navigationUILib)
    implementation(Modules.Libraries.servicesLocationLib)
    implementation(Modules.Libraries.mapsUtilLib)
    implementation(Modules.Libraries.easyPermissionsLib)
    implementation(Modules.Libraries.daggerHiltAndroidLib)
    kapt(Modules.Libraries.daggerHiltCompilerLib)
    testImplementation(Modules.TestLibraries.jUnitLib)
    androidTestImplementation(Modules.TestLibraries.androidxJUnitLib)
    androidTestImplementation(Modules.TestLibraries.espressoCoreLib)
}

kapt {
    correctErrorTypes = true
}
