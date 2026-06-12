plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.dexteralauncher.xtqmz"
    // packageName = "com.aistudio.dexteralauncher.xtqmz"
    // versionNumber = 2
    minSdk = 24
    targetSdk = 36
    versionCode = 3
    versionName = "3.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // Guarantee the field exists for compilation in all environments (secrets plugin will provide real value when .env present)
    buildConfigField("String", "GEMINI_API_KEY", "\"MY_GEMINI_API_KEY\"")
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      val localKeystore = file("${rootDir}/debug.keystore")
      val defaultKeystore = file("${System.getProperty("user.home")}/.android/debug.keystore")
      storeFile = if (localKeystore.exists()) localKeystore else defaultKeystore
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      // === MAJOR SIZE & PERFORMANCE OPTIMIZATIONS ===
      isCrunchPngs = false
      isMinifyEnabled = true          // R8: shrink, optimize, obfuscate
      isShrinkResources = true        // Remove unused resources (pairs great with minify)
      isDebuggable = false
      isProfileable = true            // Allow profiling of release builds (useful for Baseline Profiles)
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "src/main/proguard-rules.pro"   // Project-specific rules (moved into src/main for cleanliness)
      )
      // Use debug keystore for local "release" testing if the real upload key is not present.
      // Remove this fallback (or set KEYSTORE_PATH etc.) for real Play Store uploads.
      signingConfig = if (file(System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks").exists()) {
        signingConfigs.getByName("release")
      } else {
        signingConfigs.getByName("debugConfig")
      }

      // Extra packaging optimizations for smaller/faster APKs
      packaging {
        resources {
          excludes += listOf(
            "META-INF/*.version",
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE*",
            "META-INF/NOTICE*",
            "META-INF/ASL2.0",
            "META-INF/LGPL2.1",
            "META-INF/gradle/incremental.annotation.processors",
            "META-INF/*.properties",
            "META-INF/*.xml",
            "META-INF/proguard/*",
            "META-INF/rxjava.properties",
            "**/attach_hotspot_windows.dll",
            "META-INF/versions/9/module-info.class"
          )
        }
        // dex and jni compression (Play App Bundle handles this well)
        jniLibs {
          useLegacyPackaging = false
        }
        dex {
          useLegacyPackaging = false
        }
      }
    }
    debug {
      isMinifyEnabled = true
      isShrinkResources = true
      isDebuggable = true
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    // isCoreLibraryDesugaringEnabled can be enabled later with a compatible JDK + desugar_jdk_libs
  }

  buildFeatures {
    compose = true
    buildConfig = true
    // viewBinding and dataBinding left disabled (Compose only project)
  }
  lint {
    abortOnError = false
    checkReleaseBuilds = false
    // You can enable specific checks in CI if desired:
    // disable += listOf("MissingTranslation", "UnusedResources")
  }

  testOptions { unitTests { isIncludeAndroidResources = true } }
  buildToolsVersion = "37.0.0"

  // === BASELINE PROFILES (huge cold-start win for a launcher) ===
  // Place optimized profile at: src/main/baselineProfiles/baseline-prof.txt
  // It will be merged into the release APK/AAB when minify is enabled.
  // Combined with ProfileInstaller dependency above, this pre-compiles critical paths.

  // Top-level packaging options (applies to all variants, release overrides more)
  packaging {
    resources {
      excludes += listOf(
        "**/LICENSE.txt",
        "**/LICENSE",
        "**/NOTICE",
        "META-INF/CHANGES",
        "META-INF/CHANGES.txt"
      )
    }
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)

  // === RUNTIME + STARTUP OPTIMIZATIONS ===
  // ProfileInstaller + Baseline Profiles = dramatically faster cold starts for launchers
  implementation("androidx.profileinstaller:profileinstaller:1.4.1")
}
