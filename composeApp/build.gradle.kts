import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()
    
    js(IR) {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.components.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.bouncycastle)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.ui.tooling.preview)
            implementation(libs.material.icons)
            implementation(libs.material3.window.size)

            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.navigation3.ui)
            implementation(libs.navigation3.viewmodel)
            implementation(libs.navigation.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.navigation3)

            implementation(libs.crypto.core)
            implementation(libs.crypto.provider)
            implementation(libs.crypto.random)

            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.coil)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings.serialization)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.bouncycastle)
        }
        nativeMain.dependencies {
        }
        jsMain {
            dependencies {
                dependencies {
                    implementation(npm("@stablelib/chacha20poly1305", "2.0.1"))
                    implementation(npm("@stablelib/x25519", "2.0.1"))
                    implementation(npm("@stablelib/hkdf", "2.0.1"))
                    implementation(npm("@stablelib/sha256", "2.0.1"))
                    implementation(npm("@stablelib/hex", "2.0.1"))
                }
            }
        }
    }
}

android {
    namespace = "com.tukangencrypt.stegasaurus"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.tukangencrypt.stegasaurus"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 8
        versionName = "2.3.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.ui.tooling)
}

compose.desktop {
    application {
        buildTypes.release {
            proguard {
                isEnabled.set(false)
            }
        }
        mainClass = "com.tukangencrypt.stegasaurus.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Stegasaurus"
            packageVersion = "2.3.2"

            val buildType = project.findProperty("compose.desktop.buildType") ?: "release"
            if (buildType == "release") {
                includeAllModules = false
            }

            modules(
                "java.desktop",           // UI
                "java.sql",               // Database jika pakai
                "jdk.crypto.ec",          // Elliptic Curve crypto
                "jdk.crypto.cryptoki"     // Crypto provider
                // Hapus jdk.security.auth jika tidak pakai JAAS
            )
            includeAllModules = false  // Important! Jangan bundle semua

            description = "Stegasaurus — Steganography signcryption tool"
            vendor = "TukangEncrypt"
            copyright = "© 2025"

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            linux {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/ic_launcher.png"))
                shortcut = true
                menuGroup = "Stegasaurus"
                debMaintainer = "Andreas M Lbn Gaol <lgandre45@gmail.com>"
                appCategory = "Utility"
            }

            windows {
                menuGroup = "Stegasaurus"
                shortcut = true
                iconFile.set(project.file("src/commonMain/composeResources/drawable/ic_launcher.png"))
                perUserInstall = true
                dirChooser = true
            }
        }
    }
}

tasks.withType<Jar> {
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

    // Exclude unused files
    exclude("META-INF/maven/**")
    exclude("META-INF/proguard/**")
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_builtins")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
