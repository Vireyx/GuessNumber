// Top-level build file
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.2")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}