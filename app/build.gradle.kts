plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "strss.no.echoesoftheforgottenvale"
    compileSdk = 35

    defaultConfig {
        applicationId = "strss.no.echoesoftheforgottenvale"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    
    bundle {
        language { enableSplit = false }
        density { enableSplit = false }
        abi { enableSplit = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

tasks.register("fixResources") {
    doLast {
        val fontDir = file("src/main/res/font")
        val drawableDir = file("src/main/res/drawable")
        if (!drawableDir.exists()) drawableDir.mkdirs()
        
        fontDir.listFiles()?.forEach { file ->
            if (file.name.endsWith(".png")) {
                val target = File(drawableDir, file.name)
                println("Moving ${file.name} to ${target.absolutePath}")
                file.copyTo(target, overwrite = true)
                if (file.delete()) {
                    println("Successfully moved and deleted original ${file.name}")
                } else {
                    println("Failed to delete original ${file.name}")
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
