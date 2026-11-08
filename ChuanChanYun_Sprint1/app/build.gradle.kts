plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "cn.scyun"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "cn.scyun"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        // 迭代 1 的版本号：后续 7 次迭代在同一工程上叠加，最终为 1.0.0
        versionName = "0.1.0-sprint1"
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
        encoding = "utf-8"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    // 迭代 1 只用这两个库：AppCompat（Activity / AlertDialog）+ ConstraintLayout（第二种布局实现）
    // 不引入 Material / RecyclerView / SQLite / 网络库，保证本次代码只覆盖第 1–2 章知识点。
}
